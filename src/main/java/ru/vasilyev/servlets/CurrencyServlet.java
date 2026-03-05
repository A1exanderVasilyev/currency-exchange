package ru.vasilyev.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vasilyev.models.Currency;
import ru.vasilyev.services.CurrencyService;
import ru.vasilyev.utils.Utils;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        int minimumValidSize = 4;
        if (path == null || path.equals("/") || path.length() < minimumValidSize) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is not valid");
            return;
        }

        String currencyCode = Utils.getCurrencyCodeFromPath(path, 1, 4);
        try {
            Currency currency = currencyService.getCurrencyByCode(currencyCode);
            if (currency == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Currency not found");
                return;
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(resp.getOutputStream(), currency);

        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
