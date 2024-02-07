package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");

        try {
            if (timezone != null) {
                ZoneId.of(timezone);
            }
            chain.doFilter(req, res);
        } catch (DateTimeException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("Invalid timezone");
        }
    }
}
