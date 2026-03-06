package ru.vasilyev.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.vasilyev.dto.Exchange;
import ru.vasilyev.models.ExchangeRate;
import ru.vasilyev.services.ExchangeRateService;
import ru.vasilyev.utils.Utils;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        if (Utils.isCurrencyCodesNotValid(from, to)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code not valid");
            return;
        }

        BigDecimal amountBD = Utils.parseStringToBigDecimal(amount);

        if (amountBD == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid amount");
            return;
        }

        try {
            Exchange exchange = accumulateExchange(from, to, amountBD);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), exchange);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Exchange accumulateExchange(String base, String target, BigDecimal amountBD) throws IOException {
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCodePair(base, target);
        if (exchangeRate != null) {
            return buildExchange(exchangeRate, amountBD);
        }

        exchangeRate = exchangeRateService.getExchangeRateByCodePair(target, base);
        if (exchangeRate != null) {
            ExchangeRate reversedRate = makeReverseExchangeRate(exchangeRate);
            return buildExchange(reversedRate, amountBD);
        }

        ExchangeRate fromUSDtoBase = exchangeRateService.getExchangeRateByCodePair("USD", base);
        ExchangeRate fromUSDtoTarget = exchangeRateService.getExchangeRateByCodePair("USD", target);
        if (fromUSDtoBase == null || fromUSDtoTarget == null) {
            throw new IllegalArgumentException("Exchange rate not found");
        }

        BigDecimal crossRate = fromUSDtoTarget.getRate()
                .divide(fromUSDtoBase.getRate(), 6, RoundingMode.HALF_EVEN);
        ExchangeRate crossExchangeRate = new ExchangeRate(
                fromUSDtoBase.getTargetCurrency(),
                fromUSDtoTarget.getTargetCurrency(),
                crossRate
        );
        return buildExchange(crossExchangeRate, amountBD);
    }

    private static ExchangeRate makeReverseExchangeRate(ExchangeRate exchangeRate) {
        return new ExchangeRate(
                exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                BigDecimal.ONE.divide(exchangeRate.getRate(), 2, RoundingMode.HALF_UP)
        );
    }

    private Exchange buildExchange(ExchangeRate exchangeRate, BigDecimal amountBD) {
        BigDecimal convertedAmount = amountBD.multiply(exchangeRate.getFullRate())
                .setScale(2, RoundingMode.HALF_UP);

        return new Exchange(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amountBD,
                convertedAmount
        );
    }
}
