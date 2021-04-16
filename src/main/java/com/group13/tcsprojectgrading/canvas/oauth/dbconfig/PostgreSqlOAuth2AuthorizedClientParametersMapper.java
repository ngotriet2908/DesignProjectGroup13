package com.group13.tcsprojectgrading.canvas.oauth.dbconfig;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService.OAuth2AuthorizedClientHolder;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService.OAuth2AuthorizedClientParametersMapper;

import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Switches the type blob to type binary in order to make Postgres work with OAuth2
 */
public class PostgreSqlOAuth2AuthorizedClientParametersMapper extends OAuth2AuthorizedClientParametersMapper {

    @Override
    public List<SqlParameterValue> apply(OAuth2AuthorizedClientHolder authorizedClientHolder) {
        return super.apply(authorizedClientHolder).stream()
                .map(parameter -> parameter.getSqlType() == Types.BLOB
                        ? new SqlParameterValue(Types.BINARY, parameter.getValue()) : parameter)
                .collect(Collectors.toList());
    }
}