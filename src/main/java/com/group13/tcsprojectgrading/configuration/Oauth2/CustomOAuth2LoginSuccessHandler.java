package com.group13.tcsprojectgrading.configuration.Oauth2;

import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Participant;
import com.group13.tcsprojectgrading.service.canvasFetching.CanvasCourseService;
import com.group13.tcsprojectgrading.service.canvasFetching.CanvasUserService;
import com.group13.tcsprojectgrading.service.project.ProjectService;
import com.group13.tcsprojectgrading.service.user.AccountService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.group13.tcsprojectgrading.model.user.ApplicationUserRole.*;

@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    protected Log logger = LogFactory.getLog(this.getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private CanvasUserService canvasUserService;

    @Autowired
    private CanvasCourseService canvasCourseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProjectService projectService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    protected String determineTargetUrl(final Authentication authentication) {

        Map<String, String> roleTargetUrlMap = new HashMap<>();
//        roleTargetUrlMap.put(TEACHER.getSimpleAuthoritiesLabel(), "/api/basic/secured1");
        roleTargetUrlMap.put(TEACHING_ASSISTANT.getSimpleAuthoritiesLabel(), "/user");

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if(roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }

        return "/";
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug(
                    "Response has already been committed. Unable to redirect to "
                            + targetUrl);
            return;
        }

        //Syncing with Canvas

        long begin = System.currentTimeMillis();

        //Sync account
        canvasUserService.addNewAccount(oauth2User.getAccessToken().getTokenValue());
        //Sync courses
        canvasUserService.selfSyncCourseAndUser(oauth2User.getAccessToken().getTokenValue(), oauth2User.getUserId());

        String couseId = "120";
        Long assignmentId = 160L;

        //Sync course members and projects
        canvasCourseService.syncParticipants(oauth2User.getAccessToken().getTokenValue(), couseId);

        canvasCourseService.syncCourseProjects(oauth2User.getAccessToken().getTokenValue(), couseId);

        //Sync group
        canvasCourseService.syncSingleCourseGroup(couseId);

        canvasCourseService.syncCourseGroupCategory(oauth2User.getAccessToken().getTokenValue(), couseId);

        canvasCourseService.syncCourseGroups(oauth2User.getAccessToken().getTokenValue(), couseId);

        canvasCourseService.syncCourseGroupParticipant(oauth2User.getAccessToken().getTokenValue(), couseId);

        //Sync submission
        List<Project> projects = projectService.getProjects();
        for (Project project: projects) {
            canvasCourseService.syncSubmission(oauth2User.getAccessToken().getTokenValue(), project.getId());
        }

        System.out.println("time: " + (System.currentTimeMillis()-begin));

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}
