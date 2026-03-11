$(document).ready(function() {
    const host = "http://localhost:8080"

    // Fetch the list of currencies and populate the select element
    function requestCurrencies() {
        $.ajax({
            url: `${host}/currencies`,
            type: "GET",
            dataType: "json",
            success: function (data) {
                const tbody = $('.currencies-table tbody');
                tbody.empty();
                $.each(data, function(index, currency) {
                    const row = $('<tr></tr>');
                    row.append($('<td></td>').text(currency.code));
                    row.append($('<td></td>').text(currency.name));
                    row.append($('<td></td>').text(currency.sign));
                    tbody.append(row);
                });

                const newRateBaseCurrency = $("#new-rate-base-currency");
                newRateBaseCurrency.empty();

                // populate the base currency select element with the list of currencies
                $.each(data, function (index, currency) {
                    newRateBaseCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });

                const newRateTargetCurrency = $("#new-rate-target-currency");
                newRateTargetCurrency.empty();

                // populate the target currency select element with the list of currencies
                $.each(data, function (index, currency) {
                    newRateTargetCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });

                const convertBaseCurrency = $("#convert-base-currency");
                convertBaseCurrency.empty();

                // populate the base currency select element with the list of currencies
                $.each(data, function (index, currency) {
                    convertBaseCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });

                const convertTargetCurrency = $("#convert-target-currency");
                convertTargetCurrency.empty();

                // populate the base currency select element with the list of currencies
                $.each(data, function (index, currency) {
                    convertTargetCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('Error loading currencies:', jqXHR.status, textStatus);
                showError('Failed to load currencies');
            }
        });
    }

    requestCurrencies();

    $("#add-currency").submit(function(e) {
        e.preventDefault();

        $.ajax({
            url: `${host}/currencies`,
            type: "POST",
            data: $("#add-currency").serialize(),
            success: function(data) {
                requestCurrencies();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                handleErrorResponse(jqXHR, 'Failed to add currency');
            }
        });

        return false;
    });

    function requestExchangeRates() {
        $.ajax({
            url: `${host}/exchangeRates`,
            type: "GET",
            dataType: "json",
            success: function(response) {
                const tbody = $('.exchange-rates-table tbody');
                tbody.empty();
                $.each(response, function(index, rate) {
                    const row = $('<tr></tr>');
                    const currency = rate.baseCurrency.code + rate.targetCurrency.code;
                    const exchangeRate = rate.rate;
                    row.append($('<td></td>').text(currency));
                    row.append($('<td></td>').text(exchangeRate));
                    row.append($('<td></td>').html(
                        '<button class="btn btn-secondary btn-sm exchange-rate-edit"' +
                        'data-bs-toggle="modal" data-bs-target="#edit-exchange-rate-modal">Edit</button>'
                    ));
                    tbody.append(row);
                });
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error loading exchange rates:', jqXHR.status, textStatus);
                showError('Failed to load exchange rates');
            }
        });
    }

    requestExchangeRates();

    $(document).delegate('.exchange-rate-edit', 'click', function() {
        const pair = $(this).closest('tr').find('td:first').text();
        const exchangeRate = $(this).closest('tr').find('td:eq(1)').text();

        $('#edit-exchange-rate-modal .modal-title').text(`Edit ${pair} Exchange Rate`);
        $('#edit-exchange-rate-modal #exchange-rate-input').val(exchangeRate);

        $('#edit-exchange-rate-modal').modal('show');
    });

    // add event handler for edit exchange rate modal "Save" button
    $('#edit-exchange-rate-modal .btn-primary').click(function() {
        const pair = $('#edit-exchange-rate-modal .modal-title').text()
            .replace('Edit ', '').replace(' Exchange Rate', '');
        let rateValue = $('#edit-exchange-rate-modal #exchange-rate-input').val();
        rateValue = rateValue.replace(',', '.');

        // Самый надежный способ - URL encoded строка
        $.ajax({
            url: `${host}/exchangeRate/${pair}`,
            type: "PATCH",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            data: `rate=${encodeURIComponent(rateValue)}`, // Кодируем значение
            success: function(response) {
                console.log('PATCH successful:', response);

                // Обновляем таблицу
                const row = $(`tr:contains(${pair})`);
                row.find('td:eq(1)').text(rateValue);

                // Закрываем модальное окно
                $('#edit-exchange-rate-modal').modal('hide');

                // Показываем успех
                showSuccess('Exchange rate updated');
            },
            error: function(jqXHR) {
                console.error('PATCH failed:', jqXHR.responseText);

                let errorMsg = 'Failed to update exchange rate';
                try {
                    const response = JSON.parse(jqXHR.responseText);
                    errorMsg = response.message || errorMsg;
                } catch(e) {
                    if (jqXHR.status === 400) {
                        errorMsg = 'Invalid rate format. Use numbers like 0.73';
                    }
                }

                showError(errorMsg);

                // Все равно закрываем модальное окно
                $('#edit-exchange-rate-modal').modal('hide');
            }
        });
    });

    $("#add-exchange-rate").submit(function(e) {
        e.preventDefault();

        $.ajax({
            url: `${host}/exchangeRates`,
            type: "POST",
            data: $("#add-exchange-rate").serialize(),
            success: function(data) {
                requestExchangeRates();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                handleErrorResponse(jqXHR, 'Failed to add exchange rate');

            }
        });

        return false;
    });

    $("#convert").submit(function(e) {
        e.preventDefault();

        const baseCurrency = $("#convert-base-currency").val();
        const targetCurrency = $("#convert-target-currency").val();
        const amount = $("#convert-amount").val();

        $.ajax({
            url: `${host}/exchange?from=${baseCurrency}&to=${targetCurrency}&amount=${amount}`,
            type: "GET",
            dataType: "json",
            contentType: "application/json",
            success: function(data) {
                if (data && data.convertedAmount !== undefined) {
                    $("#convert-converted-amount").val(data.convertedAmount);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                handleErrorResponse(jqXHR, 'Failed to convert currency');
            }
        });

        return false;
    });

    function handleErrorResponse(jqXHR, defaultMessage) {
        let message = defaultMessage;

        try {
            if (jqXHR.responseText) {
                const response = JSON.parse(jqXHR.responseText);
                message = response.message || message;
            }
        } catch (e) {
            console.log('Could not parse error response:', jqXHR.responseText?.substring(0, 100));
            if (jqXHR.status === 400) {
                message = 'Invalid data format. Check your input.';
            } else if (jqXHR.status === 404) {
                message = 'Resource not found';
            } else if (jqXHR.status === 409) {
                message = 'Resource already exists';
            } else if (jqXHR.status === 500) {
                message = 'Server error. Please try again later.';
            }
        }

        showError(message);
    }


    function showSuccess(message) {
        const toast = $('#api-error-toast');
        toast.removeClass('bg-danger').addClass('bg-success');
        toast.find('.toast-body').text(message);
        toast.toast('show');

        setTimeout(() => toast.toast('hide'), 3000);
    }

    function showError(message) {
        const toast = $('#api-error-toast');
        toast.removeClass('bg-success').addClass('bg-danger');
        toast.find('.toast-body').text(message);
        toast.toast('show');

        setTimeout(() => toast.toast('hide'), 5000);
    }

});