package com.group13.tcsprojectgrading.canvas.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Component
public class CanvasOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    protected Log logger = LogFactory.getLog(this.getClass());

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
//        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

//    protected String determineTargetUrl(final Authentication authentication) {
//        Map<String, String> roleTargetUrlMap = new HashMap<>();
////        roleTargetUrlMap.put(TEACHER.getSimpleAuthoritiesLabel(), "/api/basic/secured1");
//        roleTargetUrlMap.put(TEACHING_ASSISTANT.getSimpleAuthoritiesLabel(), "/user");
//
//        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        for (final GrantedAuthority grantedAuthority : authorities) {
//            String authorityName = grantedAuthority.getAuthority();
//            if(roleTargetUrlMap.containsKey(authorityName)) {
//                return roleTargetUrlMap.get(authorityName);
//            }
//        }
//
//        return "/";
//    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
//        String targetUrl = determineTargetUrl(authentication);

//        if (response.isCommitted()) {
//            logger.debug(
//                    "Response has already been committed. Unable to redirect to "
//                            + targetUrl);
//            return;
//        }

        //Syncing with Canvas
        long begin = System.currentTimeMillis();

//        System.out.println(oauth2User.getAttributes());

//        //Sync account
//        userSyncService.syncUser();
//
//        //Sync courses
//        coursesSyncService.selfSyncCourseAndUser(String.valueOf(oauth2User.getAttributes().get("id")));
//
//        String courseId = "120";
//        Long assignmentId = 160L;
//
//        //Sync course members and projects
//        coursesSyncService.syncParticipants(courseId);
//
//        coursesSyncService.syncCourseProjects(courseId);
//
//        //Sync group
//        coursesSyncService.syncSingleCourseGroup(courseId);
//
//        coursesSyncService.syncCourseGroupCategory(courseId);
//
//        coursesSyncService.syncCourseGroups(courseId);
//
//        coursesSyncService.syncCourseGroupParticipants(courseId);
//
//        //Sync submission
//        List<Project> projects = projectService.getProjects();
//        for (Project project: projects) {
//            coursesSyncService.syncSubmission(project.getId());
//        }

//        System.out.println("time: " + (System.currentTimeMillis()-begin));

        redirectStrategy.sendRedirect(request, response, "/");
    }

    @Bean
    public CanvasOAuth2LoginSuccessHandler canvasAuthenticationSuccessHandler(){
        return new CanvasOAuth2LoginSuccessHandler();
    }
}
