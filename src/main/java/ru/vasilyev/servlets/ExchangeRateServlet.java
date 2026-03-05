package ru.vasilyev.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vasilyev.models.ExchangeRate;
import ru.vasilyev.services.ExchangeRateService;
import ru.vasilyev.utils.Utils;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        if (isPathCodePairNotValid(path)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exchange rate code pair");
            return;
        }
        String[] exchangeRatePair = Utils.getCurrencyPairFromPath(path);

        try {
            ExchangeRate exchangeRate = exchangeRateService
                    .getExchangeRateByCodePair(exchangeRatePair[0],  exchangeRatePair[1]);
            if (exchangeRate == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
                return;
            }
            objectMapper.writeValue(resp.getOutputStream(), exchangeRate);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }

    private boolean isPathCodePairNotValid(String path) {
        // example: /USDRUB
        int pathWithCodePairLength = 7;
        return path == null || path.isEmpty() || path.length() < pathWithCodePairLength;
    }
}
