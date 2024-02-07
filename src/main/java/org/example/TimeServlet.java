package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    private static final String DEFAULT_TIMEZONE = "UTC";
    private static final String LAST_TIMEZONE = "lastTimezone";

    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getServletContext().getRealPath("/templates/"));
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);

        engine.setTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timezone = req.getParameter("timezone");

        if (timezone == null) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (var cookie : cookies) {
                    if (cookie.getName().equals(LAST_TIMEZONE)) {
                        timezone = cookie.getValue();
                        break;
                    }
                }
            }
        } else {
            Cookie cookie = new Cookie(LAST_TIMEZONE, timezone);
            resp.addCookie(cookie);
        }

        ZoneId zoneId = ZoneId.of(timezone != null ? timezone : DEFAULT_TIMEZONE);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Context context = new Context();
        context.setVariable("dateTime", dtf.format(now));
        context.setVariable("zoneId", zoneId.getId());

        engine.process("time", context, resp.getWriter());
        resp.getWriter().close();
    }

    @Override
    public void destroy() {
        engine = null;
    }
}
