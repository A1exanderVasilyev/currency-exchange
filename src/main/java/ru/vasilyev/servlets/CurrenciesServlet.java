package ru.vasilyev.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vasilyev.models.Currency;
import ru.vasilyev.services.CurrencyService;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            objectMapper.writeValue(resp.getOutputStream(), currencyService.getAllCurrencies());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error while getting currencies: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (isParametersNotValid(code, name, sign)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameters not valid");
            return;
        }
        try {
            if (currencyService.getCurrencyByCode(code) != null) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Currency with that code already exists");
                return;
            }
            Currency savedCurrency = currencyService.saveCurrency(new Currency(code, name, sign));
            objectMapper.writeValue(resp.getOutputStream(), savedCurrency);
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private boolean isParametersNotValid(String code, String name, String sign) {
        if (isAnyParamsEmpty(code, name, sign)) {
            return true;
        }
        // db constraints
        int codeLength = 3;
        int maxNameLength = 100;
        int maxSignLength = 5;

        return code.length() != codeLength
                || name.length() > maxNameLength
                || sign.length() > maxSignLength;
    }

    private boolean isAnyParamsEmpty(String code, String name, String sign) {
        return code == null || code.isEmpty()
                || name == null || name.isEmpty()
                || sign == null || sign.isEmpty();
    }
}
