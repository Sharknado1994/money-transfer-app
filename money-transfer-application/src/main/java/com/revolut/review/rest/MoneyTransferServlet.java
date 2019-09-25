package com.revolut.review.rest;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

@Slf4j
public class MoneyTransferServlet extends HttpServlet {
    private static Logger LOGGER = Logger.getLogger(MoneyTransferServlet.class.getName());

    protected void doGet(
            HttpServletRequest request, final HttpServletResponse response) {
        LOGGER.info("Start request");
        final AsyncContext async = request.startAsync();
        async.start(() -> {
            final String sourceAccount = request.getParameter("src");
            final String targetAccount = request.getParameter("trg");
            final String value = request.getParameter("value");

            try {
                response.getWriter().println(sourceAccount + " " + targetAccount + " " + value);
            } catch (IOException e) {
                LOGGER.severe("Error " + e.getMessage());
                e.printStackTrace();
            } finally {
                LOGGER.info("End request");
                async.complete();
            }
        });
    }
}
