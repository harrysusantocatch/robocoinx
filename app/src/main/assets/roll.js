function () {
var fingerprint = $.fingerprint();
    $("#free_play_form_button").click(function(event) {
            event.preventDefault();
            $('#free_play_digits').show();
            $('.free_play_element').hide();
            var fingerprint2 = new Fingerprint({
                canvas: true,
                screen_resolution: true,
                ie_activex: true
            }).get();
            var token = $("#free_play_form").find('[name="' + token_name + '"]').val();
            var intervalID1 = setInterval(function() {
                $("#free_play_first_digit").html(Math.floor(Math.random() * 10));
            }, 10);
            var intervalID2 = setInterval(function() {
                $("#free_play_second_digit").html(Math.floor(Math.random() * 10));
            }, 10);
            var intervalID3 = setInterval(function() {
                $("#free_play_third_digit").html(Math.floor(Math.random() * 10));
            }, 10);
            var intervalID4 = setInterval(function() {
                $("#free_play_fourth_digit").html(Math.floor(Math.random() * 10));
            }, 10);
            var intervalID5 = setInterval(function() {
                $("#free_play_fifth_digit").html(Math.floor(Math.random() * 10));
            }, 10);
            $("#free_play_form_button").attr("disabled", true);
            $("html, body").animate({
                scrollTop: $("#free_play_digits").offset().top - 50
            }, "fast");
            var post_variables = {
                op: 'free_play',
                fingerprint: fingerprint,
                client_seed: $('#next_client_seed').val(),
                fingerprint2: fingerprint2,
                pwc: $('#pwc_input').val(),
            };
            post_variables[token_name] = token;
            post_variables[tcGiQefA] = window[tcGiQefA];
            if ($("#free_play_recaptcha") && $("#free_play_recaptcha").length > 0) {
                if (typeof grecaptcha !== 'undefined') {
                    post_variables['g_recaptcha_response'] = encodeURIComponent(grecaptcha.getResponse());
                }
            }
            if ($('#captchasnet_free_play_captcha .captchasnet_captcha_input_box').val() && $('#captchasnet_free_play_captcha .captchasnet_captcha_input_box').val().length > 0) {
                post_variables['captchasnet_random'] = $('#captchasnet_free_play_captcha .captchasnet_captcha_random').val();
                post_variables['captchasnet_response'] = $('#captchasnet_free_play_captcha .captchasnet_captcha_input_box').val();
            }
            if ($('#botdetect_free_play_captcha2 .captchasnet_captcha_input_box').val() && $('#botdetect_free_play_captcha2 .captchasnet_captcha_input_box').val().length > 0) {
                post_variables['botdetect_random2'] = $('#botdetect_free_play_captcha2 .captchasnet_captcha_random').val();
                post_variables['botdetect_response2'] = $('#botdetect_free_play_captcha2 .captchasnet_captcha_input_box').val();
            }
            if ($("#free_play_form").find('#adcopy_response').val() && $("#free_play_form").find('#adcopy_response').val().length > 0) {
                post_variables['solvemedia_challenge'] = $("#free_play_form").find('#adcopy_challenge').val();
                post_variables['solvemedia_response'] = $("#free_play_form").find('#adcopy_response').val();
            }
            if ($('#securimage_free_play_captcha .captchasnet_captcha_input_box').val() && $('#securimage_free_play_captcha .captchasnet_captcha_input_box').val().length > 0) {
                post_variables['securimage_random'] = $('#securimage_free_play_captcha .captchasnet_captcha_random').val();
                post_variables['securimage_response'] = $('#securimage_free_play_captcha .captchasnet_captcha_input_box').val();
            }
            if ($('#botdetect_free_play_captcha .captchasnet_captcha_input_box').val() && $('#botdetect_free_play_captcha .captchasnet_captcha_input_box').val().length > 0) {
                post_variables['botdetect_random'] = $('#botdetect_free_play_captcha .captchasnet_captcha_random').val();
                post_variables['botdetect_response'] = $('#botdetect_free_play_captcha .captchasnet_captcha_input_box').val();
            }
            var posting = $.post('/', post_variables);
            posting.done(function(data) {
                var result = data.split(":");
                $('#free_play_error').html("");
                $('#free_play_error').hide();
                if (result[0] == "e") {
                    clearInterval(intervalID1);
                    clearInterval(intervalID2);
                    clearInterval(intervalID3);
                    clearInterval(intervalID4);
                    clearInterval(intervalID5);
                    $("#free_play_first_digit").html(0);
                    $("#free_play_second_digit").html(0);
                    $("#free_play_third_digit").html(0);
                    $("#free_play_fourth_digit").html(0);
                    $("#free_play_fifth_digit").html(0);
                    $('.free_play_element').show();
                    $('#free_play_error').show();
                    $('#free_play_error').html(result[1]);
                    if ($("#free_play_recaptcha") && $("#free_play_recaptcha").length > 0) {
                        if (typeof grecaptcha !== 'undefined') {
                            grecaptcha.reset();
                        }
                    }
                    if ($("#captchasnet_free_play_captcha") && $("#captchasnet_free_play_captcha").length > 0) {
                        GenerateCaptchasNetCaptcha('captchasnet_free_play_captcha', 0);
                    }
                    if ($("#captchasnet_free_play_captcha2") && $("#captchasnet_free_play_captcha2").length > 0) {
                        GenerateCaptchasNetCaptcha('captchasnet_free_play_captcha2', 0);
                    }
                    if ($("#free_play_solvemedia") && $("#free_play_solvemedia").length > 0 && $("#free_play_solvemedia:visible").length > 0) {
                        if (typeof ACPuzzle !== 'undefined') {
                            ACPuzzle.reload();
                        }
                    }
                    if ($("#botdetect_free_play_captcha") && $("#botdetect_free_play_captcha").length > 0) {
                        GenerateCaptchasNetCaptcha('botdetect_free_play_captcha', 3);
                    }
                    if ($("#botdetect_free_play_captcha2") && $("#botdetect_free_play_captcha2").length > 0) {
                        GenerateCaptchasNetCaptcha('botdetect_free_play_captcha2', 3);
                    }
                    if ($("#securimage_free_play_captcha") && $("#securimage_free_play_captcha").length > 0) {
                        GenerateCaptchasNetCaptcha('securimage_free_play_captcha', 2);
                    }
                    if (result[3] == "e1") {
                        $('#free_play_error').hide();
                        $('.free_play_element').hide();
                        $('#wait').show();
                        $('#same_ip_error').show();
                        $('#same_ip_error').html(result[1]);
                        $('#time_remaining').countdown({
                            until: +result[2],
                            format: 'MS'
                        });
                        setTimeout(function() {
                            RefreshPageAfterFreePlayTimerEnds();
                        }, parseInt(result[2]) * 1000);
                        title_countdown(parseInt(result[2]));
                    }
                } else if (result[0] == "s") {
                    var number = result[1];
                    var single_digit = number.split("");
                    if (number.toString().length < 5) {
                        var remaining = 5 - number.toString().length;
                        for (var i = 0; i < remaining; i++) {
                            single_digit.unshift('0');
                        }
                    }
                    clearInterval(intervalID1);
                    clearInterval(intervalID2);
                    clearInterval(intervalID3);
                    clearInterval(intervalID4);
                    clearInterval(intervalID5);
                    $("#free_play_fifth_digit").html(single_digit[4]);
                    $("#free_play_fourth_digit").html(single_digit[3]);
                    $("#free_play_third_digit").html(single_digit[2]);
                    $("#free_play_second_digit").html(single_digit[1]);
                    $("#free_play_first_digit").html(single_digit[0]);
                    $.cookie.raw = true;
                    $.cookie('last_play', result[4], {
                        expires: 3650,
                        secure: true
                    });
                    $.removeCookie('ivp7GpJPvMtG');
                    $('.free_play_element').hide();
                    $('#free_play_result').show();
                    $('#wait').show();
                    $('#balance').html(result[2]);
                    balanceChanged();
                    $('#time_remaining').countdown({
                        until: +3600,
                        format: 'MS'
                    });
                    setTimeout(function() {
                        RefreshPageAfterFreePlayTimerEnds();
                    }, 3600 * 1000);
                    title_countdown(3600);
                    free_play_claim_amount = parseFloat(Math.round(result[3] * 100000000) / 100000000).toFixed(8);
                    $('#winnings').html(free_play_claim_amount);
                    $('#balance_usd').html(result[5]);
                    $('#next_server_seed_hash').val(result[6]);
                    $('#next_nonce').html(result[8]);
                    $('.previous_server_seed').html(result[9]);
                    $('#previous_server_seed_hash').val(result[10]);
                    $('.previous_client_seed').html(result[11]);
                    $('.previous_nonce').html(result[12]);
                    $('#previous_roll').html(result[1]);
                    $('#no_previous_rolls_msg').hide();
                    $('#previous_rolls_table').show();
                    $('#previous_roll_strings').show();
                    $("#verify_rolls_link").attr("href", "https://s3.amazonaws.com/roll-verifier/verify.html?server_seed=" + result[9] + "&client_seed=" + result[11] + "&server_seed_hash=" + result[10] + "&nonce=" + result[12]);
                    $('#user_lottery_tickets').html(ReplaceNumberWithCommas(result[13]));
                    $('.user_reward_points').html(ReplaceNumberWithCommas(result[14]));
                    $('#fp_lottery_tickets_won').html(result[15]);
                    $('#fp_reward_points_won').html(result[16]);
                    $('#fp_multiplier_bonus').html(result[17]);
                    $('#fp_bonus_req_completed').html(result[18]);
                    if (parseInt(result[1]) > 9997) {
                        var fp_win_amt = 20;
                        if (parseInt(result[1]) > 9999) {
                            fp_win_amt = 200;
                        }
                        $('#make_extra_5_msg').show();
                        $('#fp_forum_msg').html('[b]I just won $' + fp_win_amt + ' at FreeBitco.in![/b]&#13;&#10;&#13;&#10;My user id is ' + socket_userid + '.&#13;&#10;&#13;&#10;My winning seeds: ' + "https://s3.amazonaws.com/roll-verifier/verify.html?server_seed=" + result[9] + "&client_seed=" + result[11] + "&server_seed_hash=" + result[10] + "&nonce=" + result[12]);
                    }
                    setTimeout(function() {
                        $('.show_multiply_modal').click();
                    }, 2000);
                }
                $("#free_play_form_button").attr("disabled", false);
            });
    });
}