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
import java.math.BigDecimal;
import java.util.function.Function;

import static ru.vasilyev.utils.Utils.parseRateToBigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, this::processGet);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, exchangeRate -> processPatch(req, exchangeRate));
    }

    private void handleRequest(HttpServletRequest req,
                               HttpServletResponse resp,
                               Function<ExchangeRate, ExchangeRate> process) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String[] exchangeRatePair = extractExchangeRatePair(req, resp);
        if (exchangeRatePair == null) {
            return;
        }

        try {
            ExchangeRate exchangeRate = exchangeRateService
                    .getExchangeRateByCodePair(exchangeRatePair[0], exchangeRatePair[1]);
            if (exchangeRate == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
                return;
            }

            ExchangeRate result = process.apply(exchangeRate);
            objectMapper.writeValue(resp.getOutputStream(), result);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }

    private ExchangeRate processGet(ExchangeRate exchangeRate) {
        return exchangeRate;
    }

    private ExchangeRate processPatch(HttpServletRequest req,
                                      ExchangeRate exchangeRate) {
        String rate = req.getParameter("rate");
        if (rate == null || rate.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid exchange rate code");
        }
        BigDecimal bigDecimalRate = parseRateToBigDecimal(rate.trim().replaceAll(",", "."));
        if (bigDecimalRate == null) {
            throw new IllegalArgumentException("Invalid exchange rate code");
        }
        exchangeRate.setRate(bigDecimalRate);
        exchangeRateService.updateExchangeRate(exchangeRate);
        return exchangeRate;
    }

    private String[] extractExchangeRatePair(HttpServletRequest req,
                                             HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (!isPathCodePairValid(path)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exchange rate code pair");
            return null;
        }
        return Utils.getCurrencyPairFromPath(path);
    }


    private boolean isPathCodePairValid(String path) {
        // example: /USDRUB
        int codePairLength = 7;
        if (path == null || path.isEmpty()) return false;
        return path.length() >= codePairLength && path.matches("/[a-zA-Z]{6}");
    }
}
