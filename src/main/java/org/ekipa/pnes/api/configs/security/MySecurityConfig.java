package org.ekipa.pnes.api.configs.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class MySecurityConfig extends WebSecurityConfigurerAdapter {

    protected static List<String> whitelist;

    protected static Set<Endpoint> getAllEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        for (Controller c : Controller.getAccessibleControllers()) {
            RequestMapping controllerMapping = Arrays.stream(c.getClass().getDeclaredAnnotationsByType(RequestMapping.class)).findFirst().orElse(null);
            String controllerPath;
            try {
                controllerPath = Arrays.stream(controllerMapping.value()).findFirst().orElse("");
            } catch (Exception ex) {
                controllerPath = "";
            }
            for (Method m : c.getClass().getDeclaredMethods()) {
                SecuredMapping securedMapping = Arrays.stream(m.getDeclaredAnnotationsByType(SecuredMapping.class)).findFirst().orElse(null);
                if (securedMapping != null) {
                    endpoints.add(new Endpoint(controllerPath + securedMapping.path(), securedMapping.role(), securedMapping.method()));
                }
            }
        }
        return new HashSet<>(endpoints);
    }

    protected HttpSecurity setupEndpoints(HttpSecurity http) throws Exception {
        Set<Endpoint> endpointList = getAllEndpoints();
        Set<String> roles = getRoles(endpointList);
        roles.removeIf(r -> r.equals(""));
        for (String role : roles) {
            Set<Endpoint> roleEndpoints = getEndpointsWithRole(endpointList, role);
            for (RequestMethod method : endpointList.stream().map(endpoint -> endpoint.getMethod()[0]).collect(Collectors.toSet())) {
                String[] endpoints = stringListToArray(getEndpointsWithMethod(roleEndpoints, method).stream().map(Endpoint::getPath).collect(Collectors.toList()));
                http = http.authorizeRequests().antMatchers(HttpMethod.resolve(method.name()), endpoints).hasRole(role.toUpperCase()).and();
            }
        }
        Set<Endpoint> permitAllEndpoints = getEndpointsWithRole(endpointList, "");
        List<String> endpointStrings = permitAllEndpoints.stream().map(Endpoint::getPath).collect(Collectors.toList());
        endpointStrings.addAll(whitelist);
        String[] endpointPaths = stringListToArray(endpointStrings);
        if (endpointPaths.length > 0) {
            for (RequestMethod method : permitAllEndpoints.stream().map(endpoint -> endpoint.getMethod()[0]).collect(Collectors.toSet())) {
                String[] endpoints = stringListToArray(getEndpointsWithMethod(permitAllEndpoints, method).stream().map(Endpoint::getPath).collect(Collectors.toList()));
                http = http.authorizeRequests().antMatchers(HttpMethod.resolve(method.name()), endpoints).permitAll().and();
            }
        }
        http = http.authorizeRequests().antMatchers(stringListToArray(whitelist)).permitAll().and();

        return http;

    }

    protected String[] stringListToArray(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    protected Set<Endpoint> getEndpointsWithMethod(Set<Endpoint> endpoints, RequestMethod method) {
        return endpoints.stream().filter(endpoint -> endpoint.getMethod()[0].equals(method)).collect(Collectors.toSet());
    }

    protected Set<Endpoint> getEndpointsWithRole(Set<Endpoint> endpoints, String role) {
        Set<Endpoint> roleEndpoints = new HashSet<>();
        for (Endpoint endpoint : endpoints) {
            if (Arrays.asList(endpoint.getRole()).contains(role)) {
                roleEndpoints.add(endpoint);
            }
        }
        return roleEndpoints;
    }

    protected Set<String> getRoles(Set<Endpoint> endpoints) {
        Set<String> endpointList = new HashSet<>();
        endpoints.forEach(endpoint -> endpointList.addAll(Arrays.asList(endpoint.getRole())));
        return endpointList;
    }

    protected static void addToWhitelist(String s) {
        whitelist.add(s);
    }

    protected static void clearWhitelist() {
        whitelist = new ArrayList<>();
    }

    protected static void setWhitelist(List<String> whitelist) {
        MySecurityConfig.whitelist = whitelist;
    }

    protected static void addToWhitelist(List<String> s) {
        whitelist.addAll(s);
    }
}
