package ru.vasilyev.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vasilyev.models.Currency;
import ru.vasilyev.models.ExchangeRate;
import ru.vasilyev.services.CurrencyService;
import ru.vasilyev.services.ExchangeRateService;
import ru.vasilyev.utils.Utils;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            objectMapper.writeValue(resp.getOutputStream(), exchangeRateService.getAllExchangeRates());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error in getting exchange rates");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (isCurrencyCodesNotValid(baseCurrencyCode, targetCurrencyCode)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code not valid");
            return;
        }
        try {
            Currency baseCurrency = currencyService.getCurrencyByCode(baseCurrencyCode);
            Currency targetCurrency = currencyService.getCurrencyByCode(targetCurrencyCode);

            if (baseCurrency == null || targetCurrency == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Some currency not exists");
                return;
            }

            if (exchangeRateService.isExchangeRateExists(baseCurrencyCode, targetCurrencyCode)) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Exchange rate already exists");
                return;
            }

            BigDecimal bigDecimalRate = parseRateToBigDecimal(rate.trim().replaceAll(",", "."));
            if (bigDecimalRate == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid rate format");
                return;
            }

            objectMapper.writeValue(resp.getOutputStream(),
                    exchangeRateService.saveExchangeRate(
                            new ExchangeRate(baseCurrency, targetCurrency, bigDecimalRate)
                    ));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving exchange rates");
        }
    }

    private BigDecimal parseRateToBigDecimal(String rate) {
        try {
            return new BigDecimal(rate);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isCurrencyCodesNotValid(String baseCurrencyCode, String targetCurrencyCode) {
        if (Utils.isAnyParamsEmpty(new String[]{baseCurrencyCode, targetCurrencyCode})) {
            return true;
        }

        int codeLength = 3;

        return baseCurrencyCode.length() != codeLength ||
                targetCurrencyCode.length() != codeLength;
    }

}
