var stop_autobet = false;
var autobet_dnr = false;
var autobet_running = false;
var free_play_sound = false;
var detached_captcha;
var autobet_history = [];
var submissionEnabled = true;
var bet_history_page = 0;
var jackpot_costs = ["", "0.00000002", "0.00000013", "0.00000125", "0.00001250", "0.00012500"];
var se_msg_timeout_id;
var bonus_table_closed = 0;
var hide_pending_payments = 0;
var hide_pending_deposits = 0;
var profile_withdraw_address = "";
var withdraw_max_amount = 0;
eval("var " + window[tcGiQefA] + " = ''");
var balance_last_changed = 0;
var wagering_contest_winners_round_display = current_contest_round - 1;
var parimutuel_all_events_json = "";
var parimutuel_bet_history_json = "";
var countup_setintervals = {};
var new_user_first_load = 1;
var user_stats_loaded = 0;
var fingerprint = $.fingerprint();
var daily_jp_countup_stop = 0;
var user_daily_jp_rank = 0;
var user_daily_jp_wagered = 0;
var rp_rewards_list_loaded = 0;
$.ajaxSetup({
    data: {
        csrf_token: $.cookie('csrf_token')
    },
    beforeSend: function(xhr) {
        xhr.setRequestHeader('x-csrf-token', $.cookie('csrf_token'));
    },
    timeout: 120000
});
$.extend({
    redirectPost: function(location, args) {
        var form = '';
        $.each(args, function(key, value) {
            form += '<input type="hidden" name="' + key + '" value="' + value + '">';
        });
        $('<form action="' + location + '" method="POST">' + form + '</form>').appendTo('body').submit();
    }
});
$(document).ready(function() {
    (function(p, u, s, h, x) {
        p.pushpad = p.pushpad || function() {
            (p.pushpad.q = p.pushpad.q || []).push(arguments)
        }
        ;
        h = u.getElementsByTagName('head')[0];
        x = u.createElement('script');
        x.async = 1;
        x.src = s;
        h.appendChild(x);
    }
    )(window, document, 'https://pushpad.xyz/pushpad.js');
    pushpad('init', 6483, {
        hostname: "freebitco.in"
    });
    if (pushpad_hash != 0) {
        pushpad('uid', userid, pushpad_hash);
    }
    var time_offset = new Date().getTimezoneOffset();
    fingerprint = $.fingerprint();
    var referrer_in_url = getParameterByName('r');
    if (typeof referrer_in_url != 'undefined') {
        if (referrer_in_url.match(/^[0-9]+$/) != null) {
            $.cookie.raw = true;
            $.cookie('referrer', referrer_in_url, {
                expires: 3650,
                secure: true
            });
        }
    }
    var referrer_tag = getParameterByName('tag');
    $(".homepage_play_now_button").click(function(event) {
        window.location.replace("https://freebitco.in/static/html/one_click_signup.html?r=" + referrer_in_url + "&tag=" + referrer_tag);
    });
    $('.push_modal_image').attr("src", "https://static1.freebitco.in/images/100.png");
    if (!Date.now) {
        Date.now = function() {
            return new Date().getTime();
        }
        ;
    }
    if (userid > 0) {
        InitialUserStats();
        grecaptcha.ready(function() {
            grecaptcha.execute('6Lc1kXIUAAAAAPP7OeuycKWZ-t4br4Rh3XvqWUGd', {
                action: 'all'
            }).then(function(token) {
                $.get('/cgi-bin/api.pl?op=record_recaptcha_v3&token=' + token);
            });
        });
        $.get('/cgi-bin/api.pl?op=record_fingerprint&fingerprint=' + fingerprint);
        var user_loaded_interval = setInterval(function() {
            if (user_stats_loaded == 1) {
                UpdateStats();
                UpdateUserStats();
                clearInterval(user_loaded_interval);
            }
        }, 100);
        pushpad('tags', ['registered', time_offset]);
        $.get('/cgi-bin/api.pl?op=record_user_data&type=time_offset&value=' + time_offset);
        $.get('/cgi-bin/fp_check.pl?s=' + tcGiQefA, function(data) {
            var hash = CryptoJS.SHA256(data).toString(CryptoJS.enc.Hex);
            window[tcGiQefA] = hash;
        });
        $('#golden_ticket_lambo_main_image').attr("src", "https://static1.freebitco.in/images/sirv_backup/1556279099_rIYDQiLw.png");
        $('#golden_ticket_step1').attr("src", "https://static1.freebitco.in/images/golden_ticket/step_1.png");
        $('#golden_ticket_step2').attr("src", "https://static1.freebitco.in/images/golden_ticket/step_2.png");
        $('#golden_ticket_step3').attr("src", "https://static1.freebitco.in/images/golden_ticket/step_3.png");
        $('#logout_image').attr("src", "https://static1.freebitco.in/images/logout.png");
        $('#myModal22').css('background-image', 'url(https://static1.freebitco.in/images/mp1.jpg)');
    } else {
        UpdateStats();
        pushpad('tags', ['unregistered', time_offset]);
    }
    balanceChanged();
    RenewCookies();
    $('#wager_contest_round_display').html(wagering_contest_winners_round_display);
    $("#hide_site_message").click(function() {
        $('#common_site_message').hide();
        $.get('/?op=hide_site_message');
    });
    $("#hide_payout_message").click(function() {
        $('#common_payout_message').hide();
        $.get('/?op=hide_payout_message');
    });
    charSet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var randomString = '';
    for (var i = 0; i < 16; i++) {
        var randomPoz = Math.floor(Math.random() * charSet.length);
        randomString += charSet.substring(randomPoz, randomPoz + 1);
    }
    $('#next_client_seed').val(randomString);
    $('.tabs a').click(function() {
        if ($(this).attr('id') != "mining_link") {
            $('.tabs li').removeClass('active');
            $(this).parent().addClass('active');
        }
    });
    $('#free_play_link_li').addClass('active');
    $('#faq_tab').on('load', function() {
        $('.faq_answer').hide();
    });
    $('#faq_tab').on('click', '.faq_question', function() {
        $(this).next('.faq_answer').show();
    });
    $('#what_is_bitcoin_signup_page_read_more_link').click(function() {
        $('#what_is_bitcoin_signup_page_read_more_link').hide();
        $('#what_is_bitcoin_signup_page_more').show();
        insertBitcoinMore("what_is_bitcoin_signup_page_more", "afterBegin");
    });
    $('#provably_fair_link').click(function() {
        $("html, body").animate({
            scrollTop: $("#provably_fair").offset().top - 45
        }, "fast");
    });
    $('#auto_withdraw').change(function() {
        var $input = $(this);
        var val = 0;
        if ($input.is(":checked")) {
            val = 1;
            $('#earn_btc_aw_msg').show();
            $('#earn_btc_msg').show();
            $('#hide_earn_btc_msg').hide();
        } else {
            $('#hide_earn_btc_msg').show();
            $('#earn_btc_aw_msg').hide();
        }
        $.get('/?op=toggle_auto_withdraw&val=' + val, function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            if (result[0] == "e" && val == 1) {
                $('#earn_btc_aw_msg').hide();
                $('#hide_earn_btc_msg').show();
                $('#auto_withdraw').attr('checked', false);
            }
        });
    });
    $('#earn_btc_disable_aw').click(function() {
        $.get('/?op=toggle_auto_withdraw&val=0', function(data) {
            $('#earn_btc_aw_msg').hide();
            DisplaySEMessage("s", "Auto-withdraw disabled");
            $('#auto_withdraw').attr('checked', false);
            $('#hide_earn_btc_msg').show();
        });
    });
    $("#signup_form").submit(function(event) {
        event.preventDefault();
        $("#signup_button").prop("disabled", true);
        var $form = $(this);
        var tag = $.url().param("tag");
        var op = $form.find('input[name="op"]').val()
          , referrer = $form.find('input[name="referrer"]').val()
          , btc_address = $form.find('input[name="btc_address"]').val()
          , password = $form.find('input[name="password"]').val()
          , email = $form.find('input[name="email"]').val()
          , token = $form.find('input[name="token"]').val()
          , url = $form.attr('action');
        var post_variables = {
            op: op,
            btc_address: btc_address,
            password: password,
            email: email,
            fingerprint: fingerprint,
            referrer: referrer,
            tag: tag,
            token: token
        };
        if ($("#signup_recaptcha") && $("#signup_recaptcha").length > 0) {
            if (typeof grecaptcha !== 'undefined') {
                post_variables['g_recaptcha_response'] = encodeURIComponent(grecaptcha.getResponse());
            }
        }
        if ($('#captchasnet_signup_captcha .captchasnet_captcha_input_box').val() && $('#captchasnet_signup_captcha .captchasnet_captcha_input_box').val().length > 0) {
            post_variables['captchasnet_random'] = $('#captchasnet_signup_captcha .captchasnet_captcha_random').val();
            post_variables['captchasnet_response'] = $('#captchasnet_signup_captcha .captchasnet_captcha_input_box').val();
        }
        if ($('#captchasnet_signup_captcha2 .captchasnet_captcha_input_box').val() && $('#captchasnet_signup_captcha2 .captchasnet_captcha_input_box').val().length > 0) {
            post_variables['captchasnet_random2'] = $('#captchasnet_signup_captcha2 .captchasnet_captcha_random').val();
            post_variables['captchasnet_response2'] = $('#captchasnet_signup_captcha2 .captchasnet_captcha_input_box').val();
        }
        if ($("#signup_solvemedia").find('#adcopy_response').val() && $("#signup_solvemedia").find('#adcopy_response').val().length > 0) {
            post_variables['solvemedia_challenge'] = $("#signup_solvemedia").find('#adcopy_challenge').val();
            post_variables['solvemedia_response'] = $("#signup_solvemedia").find('#adcopy_response').val();
        }
        if ($('#securimage_signup_captcha .captchasnet_captcha_input_box').val() && $('#securimage_signup_captcha .captchasnet_captcha_input_box').val().length > 0) {
            post_variables['securimage_random'] = $('#securimage_signup_captcha .captchasnet_captcha_random').val();
            post_variables['securimage_response'] = $('#securimage_signup_captcha .captchasnet_captcha_input_box').val();
        }
        if ($('#botdetect_signup_captcha .captchasnet_captcha_input_box').val() && $('#botdetect_signup_captcha .captchasnet_captcha_input_box').val().length > 0) {
            post_variables['botdetect_random'] = $('#botdetect_signup_captcha .captchasnet_captcha_random').val();
            post_variables['botdetect_response'] = $('#botdetect_signup_captcha .captchasnet_captcha_input_box').val();
        }
        var posting = $.post(url, post_variables);
        posting.done(function(data) {
            var result = data.split(":");
            if (result[0] == "e") {
                DisplaySEMessage(result[0], result[1]);
                if ($("#signup_recaptcha") && $("#signup_recaptcha").length > 0) {
                    if (typeof grecaptcha !== 'undefined') {
                        grecaptcha.reset();
                    }
                }
                if ($("#captchasnet_signup_captcha") && $("#captchasnet_signup_captcha").length > 0) {
                    GenerateCaptchasNetCaptcha('captchasnet_signup_captcha', 0);
                }
                if ($("#captchasnet_signup_captcha2") && $("#captchasnet_signup_captcha2").length > 0) {
                    GenerateCaptchasNetCaptcha('captchasnet_signup_captcha2', 0);
                }
                if ($("#signup_solvemedia") && $("#signup_solvemedia").length > 0) {
                    if (typeof ACPuzzle !== 'undefined') {
                        ACPuzzle.reload();
                    }
                }
                if ($("#securimage_signup_captcha") && $("#securimage_signup_captcha").length > 0) {
                    GenerateCaptchasNetCaptcha('securimage_signup_captcha', 2);
                }
                if ($("#botdetect_signup_captcha") && $("#botdetect_signup_captcha").length > 0) {
                    GenerateCaptchasNetCaptcha('botdetect_signup_captcha', 3);
                }
            } else if (result[0] == "s") {
                $.cookie.raw = true;
                $.cookie('btc_address', result[1], {
                    expires: 3650,
                    secure: true
                });
                $.cookie('password', result[2], {
                    expires: 3650,
                    secure: true
                });
                $.cookie('have_account', 1, {
                    expires: 3650,
                    secure: true
                });
                window.location.replace("https://freebitco.in/?op=home");
            }
            $("#signup_button").prop("disabled", false);
        });
    });
    $('.captchasnet_captcha_info').hover(function() {
        $(this).find('.captchasnet_captcha_info_span, .arrow-up, .arrow-up-small').show();
    }, function() {
        $(this).find('.captchasnet_captcha_info_span, .arrow-up, .arrow-up-small').hide();
    });
    $("#login_button").click(function(event) {
        $("#login_button").prop("disabled", true);
        var posting = $.post('/', {
            op: 'login_new',
            btc_address: $("#login_form_btc_address").val(),
            password: $("#login_form_password").val(),
            tfa_code: $("#login_form_2fa").val(),
        });
        posting.done(function(data) {
            var result = data.split(":");
            if (result[0] == "e") {
                DisplaySEMessage(result[0], result[1]);
            } else if (result[0] == "s") {
                $.cookie.raw = true;
                $.cookie('btc_address', result[1], {
                    expires: 3650,
                    secure: true
                });
                $.cookie('password', result[2], {
                    expires: 3650,
                    secure: true
                });
                $.cookie('have_account', 1, {
                    expires: 3650,
                    secure: true
                });
                window.location.replace("https://freebitco.in/?op=home");
            }
            $("#login_button").prop("disabled", false);
        });
    });
    $("#reset_2fa_form_submit").click(function(event) {
        $("#reset_2fa_form_submit").prop("disabled", true);
        var posting = $.post('/', {
            op: 'login_new',
            type: 'reset_2fa',
            subtype: $("#reset_2fa_subtype").val(),
            btc_address: $("#forgot_2fa_email").val(),
            password: $("#forgot_2fa_password").val(),
            forgot_2fa_extra_input: $("#forgot_2fa_extra_input").val(),
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            $("#reset_2fa_form_submit").prop("disabled", false);
            if ($("#reset_2fa_subtype").val() == "mobile_ver" && $('#forgot_2fa_secret_key_container_div').is(':hidden') && result[0] == "s") {
                $('#forgot_2fa_secret_key_container_div').show();
                $('#forgot_2fa_extra_input').val('');
                $('#forgot_2fa_extra_field').html('VERIFICATION CODE');
            }
        });
    });
    $("#enable_2fa_msg").click(function() {
        SwitchPageTabs('edit');
        $("html, body").animate({
            scrollTop: $("#2fa_profile_box").offset().top - 45
        }, "fast");
        $("#2fa_profile_box").click();
    });
    $("#advertise_imp_note").click(function(event) {
        alert("Please note that the balance in your advertising account is non-refundable and cannot be transferred to your main account balance on the website so only send/transfer in what you intend to use.");
    });
    $(".free_play_link").click(function(event) {
        SwitchPageTabs('free_play');
    });
    $(".double_your_btc_link").click(function(event) {
        SwitchPageTabs('double_your_btc');
    });
    $(".slots_link").click(function(event) {
        SwitchPageTabs('slots');
    });
    $(".betting_link").click(function(event) {
        SwitchPageTabs('betting');
    });
    $(".wager_promotion_link").click(function(event) {
        SwitchPageTabs('wager_promotion');
    });
    $(".lottery_link").click(function(event) {
        SwitchPageTabs('lottery');
        $('.tabs li').removeClass('active');
        $('.tabs li').find('.lottery_link').parent().addClass('active');
    });
    $(".faq_link").click(function(event) {
        SwitchPageTabs('faq');
        document.getElementById("faq_tab").insertAdjacentHTML("afterBegin", '<h3>WEBSITE FAQ</h3><p class="faq_question bold">When will I get paid if I have Auto Withdraw enabled?</p><div class="faq_answer"><p>If you have Auto-Withdraw enabled in your account, your account balance will go into PENDING on Sunday (if it is more than the min. withdraw amount) and you will be able to see this under PENDING PAYOUT on the FREE BTC page. The Bitcoins will be sent to your Bitcoin wallet soon after. You will receive an email notifying you of the payment if you have an email address associated with your account. If you wish to know the exact time when your balance will go into pending, click on the button that says <b>WITHDRAW</b> in the <b>FREE BTC</b> page and then click on <b>AUTO</b> and you will see a timer counting down to the payout time.</p></div><p class="faq_question bold">How do I enable Auto Withdraw?</p><div class="faq_answer"><p>By clicking on the button that says <b>WITHDRAW</b> in the <b>FREE BTC</b> page, then clicking on <b>AUTO</b> and checking the box next to <b>AUTO WITHDRAW</b>. If you enable auto-withdraw after the countdown timer has run out for the current payout cycle, you will receive your payment the next week.</p></div><p class="faq_question bold">When will I get paid if I have requested a Manual Withdraw?</p><div class="faq_answer"><p>If you request a manual withdrawal, the Bitcoins will be sent to your wallet within 6 hours of you initiating the request.</p></div><p class="faq_question bold">When will I get paid if I have requested an Instant Withdraw?</p><div class="faq_answer"><p>If you request an instant withdrawal, the Bitcoins will be sent to your wallet within 15 minutes of you initiating the request.</p></div><p class="faq_question bold">How can I change my Bitcoin address or Email address?</p><div class="faq_answer"><p>By clicking on the button that says <b>PROFILE</b> in the top menu. You will be able to change your email address only if the email that is currently attached to your account is invalid or it is bouncing our emails back.</p></div><p class="faq_question bold">Where can I see my referral link or my referrals?</p><div class="faq_answer"><p>By clicking on the button that says <b>REFER</b> in the top menu.</p></div><p class="faq_question bold">How do I refer my friends?</p><div class="faq_answer"><p>Share your referral link with your friends and ask them to visit it and create an account. On doing so, they will be automatically added as your referral and you will get 50% of their free btc winnings as commission! Nothing will be deducted from their account, we pay the 50% out of our pocket. You will also receive 1 free ticket to our weekly lottery every time your friend plays a free roll! If you do not know how to get your referral link, please see the question above.</p></div><p class="faq_question bold">I have lost/wish to reset my password?</p><div class="faq_answer"><p>Please go to the login page and click on the link that says <b>Forgot Password</b> in the login box.</p></div><p class="faq_question bold">Why does the amount of Bitcoins that you can win, keep changing?</p><div class="faq_answer"><p>The amount of bitcoins that you can win with <b>FREE BTC</b> depends on the current bitcoin price and the biggest prize is fixed at US$200 and the other prizes in proportion to it. So, when the price of a bitcoin goes down, the reward amount calculated in bitcoins goes up and the other way round is also true. So, regardless of the current bitcoin price, you have a fair chance of winning US$200 in bitcoins on each roll.</p></div><p class="faq_question bold">Can you reverse a payment that has already been sent?</p><div class="faq_answer"><p>Unfortunately bitcoin payments are irreversible and so once a payment has been sent, we have no way of getting it back. You should ensure that the correct withdrawal address is specified in the <b>PROFILE</b> page before requesting a payment or enabling auto-withdraw.</p></div><p class="faq_question bold">Where can I check my activity on this website?</p><div class="faq_answer"><p>By clicking on <b>STATS</b> in the above menu and then on <b>PERSONAL STATS</b>.</p></div><p class="faq_question bold">How can I keep my account secure?</p><div class="faq_answer"><p>By using a strong password, not re-using the same password on any other website and not sharing your password with anybody else. We recommend using a password manager like <a href="https://lastpass.com/" target=_blank>LastPass</a> to create and store your passwords. If you do not use these security practices and your account gets hacked, we shall not be able to help you.</p></div><p class="faq_question bold">How can I contact you?</p><div class="faq_answer"><p>By filling in the form below. Please read the questions above before sending us an email. We receive hundreds of emails everyday and do not have the resources to reply to all of them, so we have a policy of not responding to emails asking questions that have already been answered on this page.</p>');
        insertBitcoinMore("faq_tab", "beforeEnd");
    });
    $(".refer_link").click(function(event) {
        SwitchPageTabs('refer');
    });
    $(".rewards_link").click(function(event) {
        $('.tabs li').removeClass('active');
        $('.tabs li').find('.rewards_link').parent().addClass('active');
        SwitchPageTabs('rewards');
    });
    $(".earn_btc_link").click(function(event) {
        $('.tabs li').removeClass('active');
        $('.tabs li').find('.earn_btc_link').parent().addClass('active');
        SwitchPageTabs('earn_btc');
    });
    $(".edit_link").click(function(event) {
        SwitchPageTabs('edit');
    });
    $(".news_link").click(function(event) {
        SwitchPageTabs('news');
    });
    $(".stats_link").click(function(event) {
        SwitchPageTabs('stats');
    });
    $(".golden_ticket_link").click(function(event) {
        SwitchPageTabs('golden_ticket');
    });
    $('#site_stats_button').click(function() {
        $('#personal_stats_button').show();
        $('#site_stats_button').hide();
        $('#site_stats').show();
        $('#personal_stats').hide();
    });
    $('#personal_stats_button').click(function() {
        $('#personal_stats_button').hide();
        $('#site_stats_button').show();
        $('#site_stats').hide();
        $('#personal_stats').show();
    });
    $("#double_your_btc_2x").click(function(event) {
        var bet = $("#double_your_btc_stake").val();
        var bonus_bal = parseFloat($("#bonus_account_balance").html());
        var bal = parseFloat($("#balance").html());
        var balance = parseFloat(Math.round((bonus_bal + bal) * 100000000) / 100000000).toFixed(8);
        if (bet * 2 <= balance) {
            if (bet * 2 * ($("#double_your_btc_payout_multiplier").val() - 1) <= max_win_amount) {
                $("#double_your_btc_stake").val(parseFloat(Math.round(bet * 2 * 100000000) / 100000000).toFixed(8));
            } else {
                $("#double_your_btc_stake").val(parseFloat(Math.round(max_win_amount / ($("#double_your_btc_payout_multiplier").val() - 1) * 100000000) / 100000000).toFixed(8));
            }
        } else {
            if (bet * 2 * ($("#double_your_btc_payout_multiplier").val() - 1) <= max_win_amount) {
                $("#double_your_btc_stake").val(balance);
            } else {
                $("#double_your_btc_stake").val(parseFloat(Math.round(max_win_amount / ($("#double_your_btc_payout_multiplier").val() - 1) * 100000000) / 100000000).toFixed(8));
            }
        }
        CalculateWinAmount();
    });
    $("#double_your_btc_half").click(function(event) {
        var bet = $("#double_your_btc_stake").val();
        var bonus_bal = parseFloat($("#bonus_account_balance").html());
        var bal = parseFloat($("#balance").html());
        var balance = parseFloat(Math.round((bonus_bal + bal) * 100000000) / 100000000).toFixed(8);
        if (bet * 0.5 <= balance) {
            if (bet * 0.5 * ($("#double_your_btc_payout_multiplier").val() - 1) <= max_win_amount) {
                $("#double_your_btc_stake").val(parseFloat(Math.round(bet * 0.5 * 100000000) / 100000000).toFixed(8));
            } else {
                $("#double_your_btc_stake").val(parseFloat(Math.round(max_win_amount / ($("#double_your_btc_payout_multiplier").val() - 1) * 100000000) / 100000000).toFixed(8));
            }
        } else {
            if (bet * 0.5 * ($("#double_your_btc_payout_multiplier").val() - 1) <= max_win_amount) {
                $("#double_your_btc_stake").val(balance);
            } else {
                $("#double_your_btc_stake").val(parseFloat(Math.round(max_win_amount / ($("#double_your_btc_payout_multiplier").val() - 1) * 100000000) / 100000000).toFixed(8));
            }
        }
        CalculateWinAmount();
    });
    $("#double_your_btc_max").click(function(event) {
        var conf = confirm("Are you sure you wish to bet the maximum amount? Click OK if you would like to proceed else click CANCEL.");
        if (conf == true) {
            var bet = $("#double_your_btc_stake").val();
            var bonus_bal = parseFloat($("#bonus_account_balance").html());
            var bal = parseFloat($("#balance").html());
            var balance = parseFloat(Math.round((bonus_bal + bal) * 100000000) / 100000000).toFixed(8);
            if (balance * ($("#double_your_btc_payout_multiplier").val() - 1) <= max_win_amount) {
                $("#double_your_btc_stake").val(balance);
            } else {
                $("#double_your_btc_stake").val(parseFloat(Math.round(max_win_amount / ($("#double_your_btc_payout_multiplier").val() - 1) * 100000000) / 100000000).toFixed(8));
            }
            CalculateWinAmount();
        }
    });
    $("#double_your_btc_min").click(function(event) {
        var bet = $("#double_your_btc_stake").val();
        var bonus_bal = parseFloat($("#bonus_account_balance").html());
        var bal = parseFloat($("#balance").html());
        var balance = parseFloat(Math.round((bonus_bal + bal) * 100000000) / 100000000).toFixed(8);
        if (balance >= 0.00000001) {
            $("#double_your_btc_stake").val('0.00000001');
        } else {
            $("#double_your_btc_stake").val('0.00000000');
        }
        CalculateWinAmount();
    });
    $("#double_your_btc_stake").keyup(function(event) {
        CalculateWinAmount();
    });
    $("#double_your_btc_stake").keydown(function(event) {
        $("#double_your_btc_stake").keyup();
    });
    $("#double_your_btc_stake").keypress(function(event) {
        $("#double_your_btc_stake").keyup();
    });
    $("#double_your_btc_stake").focusout(function(event) {
        $("#double_your_btc_stake").keyup();
    });
    $("#double_your_btc_bet_hi_button").click(function(event) {
        DoubleYourBTC('hi');
    });
    $("#double_your_btc_bet_lo_button").click(function(event) {
        DoubleYourBTC('lo');
    });
    $("#contact_form").submit(function(event) {
        event.preventDefault();
        $("#contact_form_button").attr("disabled", true);
        var $form = $(this)
          , op = $form.find('input[name="op"]').val()
          , name = $form.find('input[name="name"]').val()
          , email = $form.find('input[name="email"]').val()
          , message = $form.find('textarea[name="message"]').val()
          , url = $form.attr('action');
        var posting = $.post(url, {
            op: op,
            name: name,
            email: email,
            message: message
        });
        posting.done(function(data) {
            var result = data.split(":");
            $('#contact_form_error').html("");
            $('#contact_form_error').hide();
            $('#contact_form_success').html("");
            $('#contact_form_success').hide();
            $('#contact_form_name').removeClass('input-error');
            $('#contact_form_email').removeClass('input-error');
            $('#contact_form_message').removeClass('input-error');
            if (result[0] == "e1") {
                $('#contact_form_error').show();
                $('#contact_form_error').html("Please enter your name");
                $('#contact_form_name').addClass('input-error');
            }
            if (result[0] == "e2") {
                $('#contact_form_error').show();
                $('#contact_form_error').html("Invalid email address entered");
                $('#contact_form_email').addClass('input-error');
            }
            if (result[0] == "e3") {
                $('#contact_form_error').show();
                $('#contact_form_error').html("Message must be atleast 10 characters");
                $('#contact_form_message').addClass('input-error');
            }
            if (result[0] == "s1") {
                $('#contact_form_success').show();
                $('#contact_form_success').html("Message sent successfully!");
                $('#contact_form_message').val('');
            }
            $("#contact_form_button").attr("disabled", false);
        });
    });
    $("#forgot_password_button").click(function(event) {
        $("#forgot_password_button").prop("disabled", true);
        var posting = $.post('/', {
            op: 'forgot_password',
            email: $("#forgot_password_email").val(),
            captchasnet_random: $('#captchasnet_forgot_password_captcha .captchasnet_captcha_random').val(),
            captchasnet_response: $('#captchasnet_forgot_password_captcha .captchasnet_captcha_input_box').val(),
            fingerprint: fingerprint
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            $("#forgot_password_button").prop("disabled", false);
            GenerateCaptchasNetCaptcha('captchasnet_forgot_password_captcha', 0);
        });
    });
    $("#password_reset_form").submit(function(event) {
        event.preventDefault();
        $("#password_reset_form_button").attr("disabled", true);
        var a = $("#password_reset_form_btc_address").val();
        var s = $("#password_reset_form_signature").val();
        var m = $("#password_reset_form_message").val();
        var verified = verify_message(s, m);
        if (verified == a) {
            var $form = $(this)
              , op = $form.find('input[name="op"]').val()
              , btc_address = $form.find('input[name="btc_address"]').val()
              , message = $form.find('input[name="message"]').val()
              , signature = $form.find('input[name="signature"]').val()
              , url = $form.attr('action');
            var posting = $.post(url, {
                op: op,
                btc_address: btc_address,
                message: message,
                signature: signature
            });
            posting.done(function(data) {
                $('#password_reset_message').hide();
                $('#password_reset_message').html("");
                $('#password_reset_message').removeClass('green');
                $('#password_reset_message').removeClass('red');
                if (data == "e1") {
                    $('#password_reset_message').show();
                    $('#password_reset_message').html("Invalid Bitcoin Address entered");
                    $('#password_reset_message').addClass('red');
                }
                if (data == "e2") {
                    $('#password_reset_message').show();
                    $('#password_reset_message').html("Invalid Email Address");
                    $('#password_reset_message').addClass('red');
                }
                if (data == "e3") {
                    $('#password_reset_message').show();
                    $('#password_reset_message').html("Signature cannot be blank");
                    $('#password_reset_message').addClass('red');
                }
                if (data == "e4") {
                    $('#password_reset_message').show();
                    $('#password_reset_message').html("No account associated with this Bitcoin Address exists in our database");
                    $('#password_reset_message').addClass('red');
                }
                if (data == "e5") {
                    $('#password_reset_message').show();
                    $('#password_reset_message').html("An account with this email address already exists. Please use the forgot password box above instead.");
                    $('#password_reset_message').addClass('red');
                }
                if (data == "s1") {
                    $('#password_reset_message').show();
                    $('#password_reset_message').html("Password reset request queued. Please check your email inbox after 1 hour for the password reset link.");
                    $('#password_reset_message').addClass('green');
                }
            });
        } else {
            $('#password_reset_message').show();
            $('#password_reset_message').html("Incorrect signature. Please follow the instructions for signing messages above and then try again.");
            $('#password_reset_message').addClass('red');
        }
        $("#password_reset_form_button").attr("disabled", false);
    });
    $("#change_password_button").click(function() {
        var posting = $.post('/', {
            op: 'change_password',
            old_password: $('#cp_old_password').val(),
            new_password: $('#cp_new_password').val(),
            repeat_new_password: $('#cp_repeat_new_password').val(),
            tfa_code: $('#cp_tfa_code').val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            if (result[0] == "s") {
                $.cookie.raw = true;
                $.cookie('password', result[2], {
                    expires: 3650,
                    secure: true
                });
            }
        });
    });
    $("#edit_profile_button").click(function() {
        var posting = $.post('/', {
            op: 'edit_profile',
            func: 'change_btc_address',
            new_btc_address: $('#edit_profile_form_btc_address').val(),
            password: $('#cba_password').val(),
            tfa_code: $('#cba_tfa_code').val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            if (result[2] == "s1") {
                $.cookie.raw = true;
                $.cookie('btc_address', result[3], {
                    expires: 3650,
                    secure: true
                });
                $('.withdraw_btc_address').html(result[3]);
                $('.withdraw_btc_address').val(result[3]);
                profile_withdraw_address = result[3];
            } else if (result[2] == "s2") {
                $('#edit_profile_form_btc_address').val(result[3]);
            }
        });
    });
    $("#change_email_button").click(function() {
        var posting = $.post('/', {
            op: 'edit_profile',
            func: 'change_email',
            new_email: $('#edit_profile_form_email').val(),
            password: $('#ce_password').val(),
            tfa_code: $('#ce_tfa_code').val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
        });
    });
    $("#equal_share").click(function(event) {
        $("#weighted_share").attr("checked", false);
        $("#last_payout_share").attr("checked", false);
    });
    $("#weighted_share").click(function(event) {
        $("#equal_share").attr("checked", false);
        $("#last_payout_share").attr("checked", false);
    });
    $("#last_payout_share").click(function(event) {
        $("#equal_share").attr("checked", false);
        $("#weighted_share").attr("checked", false);
    });
    $('.footer_cur_year').html(new Date().getFullYear());
    $("#get_tag_stats").change(function() {
        var tag = $("#get_tag_stats").val();
        $.get('/?op=show_advanced_tag_stats&tag=' + tag, function(data) {
            $('#detailed_tag_stats_table').show();
            $('#detailed_tag_stats_table').find("tr:gt(0)").remove();
            $("#detailed_tag_stats_table").append(data);
        });
    });
    $(".button").click(function() {
        $(this).blur();
    });
    $("#as_equal_share").click(function(event) {
        $("#as_weighted_share").attr("checked", false);
        $("#as_last_payout_share").attr("checked", false);
    });
    $("#as_weighted_share").click(function(event) {
        $("#as_equal_share").attr("checked", false);
        $("#as_last_payout_share").attr("checked", false);
    });
    $("#as_last_payout_share").click(function(event) {
        $("#as_equal_share").attr("checked", false);
        $("#as_weighted_share").attr("checked", false);
    });
    $("#as_button").click(function(event) {
        var as_percent = $('#as_percent').val();
        $("#as_button").attr("disabled", true);
        var method;
        if ($("#as_equal_share").is(":checked")) {
            method = 1;
        }
        if ($("#as_weighted_share").is(":checked")) {
            method = 2;
        }
        if ($("#as_last_payout_share").is(":checked")) {
            method = 3;
        }
        $.get('/?op=set_auto_share&mode=' + method + '&as_percent=' + as_percent, function(data) {
            var result = data.split(":");
            $('#as_error').hide();
            $('#as_success').hide();
            if (result[0] == "e1") {
                $('#as_error').show();
                $('#as_error').html("Invalid auto-share percentage");
            }
            if (result[0] == "e2") {
                $('#as_error').show();
                $('#as_error').html("Invalid auto-share mode");
            }
            if (result[0] == "s1") {
                $('#as_success').show();
                $('#as_success').html("Auto-share set successfully!");
            }
            $("#as_button").attr("disabled", false);
        });
    });
    $(".withdraw_box_button").click(function() {
        $.get('/?op=get_current_address_and_balance', function(data) {
            var result = data.split(":");
            if (result[0] == "s") {
                $("#balance").html(result[2]);
                balanceChanged();
                m_w_fee = result[3];
                i_w_fee = result[4];
                $(".withdraw_btc_address").html(result[1]);
                $('#edit_profile_form_btc_address').val(result[1]);
                profile_withdraw_address = result[1];
            }
            var balance = $("#balance").html();
            var withdraw_amount = parseFloat(Math.floor((balance - 0.00000001) * 100000000) / 100000000).toFixed(8);
            withdraw_max_amount = withdraw_amount;
            $("#withdrawal_amount").val('');
            $("#instant_withdrawal_amount").val('');
            $("#manual_min_withdraw").html(parseFloat(min_withdraw).toFixed(8));
            $("#instant_min_withdraw").html(parseFloat(min_withdraw).toFixed(8));
            $(".manual_withdraw_fee").html(m_w_fee);
            $(".instant_withdraw_fee").html(i_w_fee);
            $('#manual_withdraw_btc_add').val('');
            $('#instant_withdraw_btc_add').val('');
            $("#manual_withdraw_amt_recv").html('0.00000000');
            $("#instant_withdraw_amt_recv").html('0.00000000');
        });
    });
    $(".withdraw_use_profile_address").click(function() {
        $('#manual_withdraw_btc_add').val(profile_withdraw_address);
        $('#instant_withdraw_btc_add').val(profile_withdraw_address);
    });
    $(".withdraw_all_link").click(function() {
        $('#withdrawal_amount').val(parseFloat(withdraw_max_amount - parseFloat(m_w_fee)).toFixed(8));
        $('#instant_withdrawal_amount').val(parseFloat(withdraw_max_amount - parseFloat(i_w_fee)).toFixed(8));
        $("#withdrawal_amount").keyup();
        $("#instant_withdrawal_amount").keyup();
    });
    $("#withdrawal_amount").keyup(function() {
        var wa = $("#withdrawal_amount").val();
        if (wa.indexOf(',') > -1) {
            wa = wa.replace(/,/g, '.');
            $("#withdrawal_amount").val(wa);
        }
        $("#manual_withdraw_amt_recv").html(parseFloat(parseFloat($("#withdrawal_amount").val()) + parseFloat(m_w_fee)).toFixed(8));
    });
    $("#withdrawal_amount").keypress(function() {
        $("#withdrawal_amount").keyup();
    });
    $("#withdrawal_amount").keydown(function() {
        $("#withdrawal_amount").keyup();
    });
    $("#instant_withdrawal_amount").keyup(function() {
        var wa = $("#instant_withdrawal_amount").val();
        if (wa.indexOf(',') > -1) {
            wa = wa.replace(/,/g, '.');
            $("#instant_withdrawal_amount").val(wa);
        }
        $("#instant_withdraw_amt_recv").html(parseFloat(parseFloat($("#instant_withdrawal_amount").val()) + parseFloat(i_w_fee)).toFixed(8));
    });
    $("#instant_withdrawal_amount").keypress(function() {
        $("#instant_withdrawal_amount").keyup();
    });
    $("#instant_withdrawal_amount").keydown(function() {
        $("#instant_withdrawal_amount").keyup();
    });
    $("#autobet_win_return_to_base").click(function(event) {
        $("#autobet_win_increase_bet").attr("checked", false);
    });
    $("#autobet_win_increase_bet").click(function(event) {
        $("#autobet_win_return_to_base").attr("checked", false);
    });
    $("#autobet_lose_return_to_base").click(function(event) {
        $("#autobet_lose_increase_bet").attr("checked", false);
    });
    $("#autobet_lose_increase_bet").click(function(event) {
        $("#autobet_lose_return_to_base").attr("checked", false);
    });
    $("#autobet_bet_hi").click(function(event) {
        $("#autobet_bet_lo").attr("checked", false);
        $("#autobet_bet_alternate").attr("checked", false);
    });
    $("#autobet_bet_lo").click(function(event) {
        $("#autobet_bet_hi").attr("checked", false);
        $("#autobet_bet_alternate").attr("checked", false);
    });
    $("#autobet_bet_alternate").click(function(event) {
        $("#autobet_bet_hi").attr("checked", false);
        $("#autobet_bet_lo").attr("checked", false);
    });
    $("#autobet_max_bet_reset").click(function(event) {
        $("#autobet_max_bet_stop").attr("checked", false);
    });
    $("#autobet_max_bet_stop").click(function(event) {
        $("#autobet_max_bet_reset").attr("checked", false);
    });
    $("#start_autobet").click(function(event) {
        $("#autobet_error").hide();
        $("#autobet_error").html('');
        $(".play_jackpot").prop("checked", false);
        $('.autobet_play_jackpot:checkbox:checked').map(function() {
            $(".play_jackpot:checkbox[value=" + this.value + "]").prop("checked", true);
        });
        var base_bet = $('#autobet_base_bet').val();
        var bet_odds = $('#autobet_bet_odds').val();
        var max_bet = $('#autobet_max_bet').val();
        var bet_count = $('#autobet_roll_count').val();
        var mode = "alternate";
        var autobet_win_return_to_base = 0
          , autobet_win_increase_bet_percent = 0
          , autobet_lose_return_to_base = 0
          , autobet_lose_increase_bet_percent = 0
          , autobet_win_change_odds = 0
          , autobet_lose_change_odds = 0
          , change_client_seed = 0
          , reset_after_max_bet = 0
          , rolls_played = 0
          , biggest_bet = 0
          , biggest_win = 0
          , stop_after_profit = 0
          , stop_after_loss = 0
          , session_pl = parseFloat(0).toFixed(8)
          , logging = 0
          , enable_worker = 0
          , enable_sounds = 0;
        if ($("#autobet_bet_hi").is(":checked")) {
            mode = "hi";
        }
        if ($("#autobet_bet_lo").is(":checked")) {
            mode = "lo";
        }
        if (base_bet < 0.00000001 || base_bet > max_win_amount) {
            AutoBetErrors("e1");
        } else if (bet_odds < 1.01 || bet_odds > 4750) {
            AutoBetErrors("e2");
        } else if (max_bet < 0.00000001 || max_bet > max_win_amount) {
            AutoBetErrors("e3");
        } else if (bet_count < 1) {
            AutoBetErrors("e4");
        } else if ($("#autobet_win_change_odds").is(":checked") && ($("#autobet_win_change_odds_value").val() < 1.01 || $("#autobet_win_change_odds_value").val() > 4750)) {
            AutoBetErrors("e5");
        } else if ($("#autobet_lose_change_odds").is(":checked") && ($("#autobet_lose_change_odds_value").val() < 1.01 || $("#autobet_lose_change_odds_value").val() > 4750)) {
            AutoBetErrors("e6");
        } else if ($("#stop_after_profit").is(":checked") && $("#stop_after_profit_value").val() <= 0) {
            AutoBetErrors("e7");
        } else if ($("#stop_after_loss").is(":checked") && $("#stop_after_loss_value").val() <= 0) {
            AutoBetErrors("e8");
        } else {
            stop_autobet = false;
            $("#auto_betting_button").hide();
            $("#stop_auto_betting").show();
            $("#double_your_btc_middle_section").css({
                'height': 'auto',
                'border-radius': '0 0 10px 10px',
                'padding-bottom': '20px'
            });
            $("#double_your_btc_stake").val(base_bet);
            $("#double_your_btc_payout_multiplier").val(bet_odds);
            $("#double_your_btc_payout_multiplier").keyup();
            $('#rolls_played_count').html('0');
            $('#rolls_played_count').html('0');
            $('#rolls_status').show();
            $('#autobet_highest_bet_msg').show();
            $('#autobet_highest_bet').html('0.00000000 BTC');
            $('#autobet_highest_win_msg').show();
            $('#autobet_highest_win').html('0.00000000 BTC');
            $('#autobet_pl_msg').show();
            $('#autobet_pl').addClass('green');
            $('#autobet_pl').css({
                'background-color': '#33FF33'
            });
            $('#autobet_pl').html('0.00000000 BTC');
            if ($("#autobet_win_return_to_base").is(":checked")) {
                autobet_win_return_to_base = 1;
            }
            if ($("#autobet_lose_return_to_base").is(":checked")) {
                autobet_lose_return_to_base = 1;
            }
            if ($("#autobet_win_increase_bet").is(":checked")) {
                autobet_win_increase_bet_percent = $("#autobet_win_increase_bet_percent").val();
            }
            if ($("#autobet_lose_increase_bet").is(":checked")) {
                autobet_lose_increase_bet_percent = $("#autobet_lose_increase_bet_percent").val();
            }
            if ($("#autobet_win_change_odds").is(":checked")) {
                autobet_win_change_odds = $("#autobet_win_change_odds_value").val();
            }
            if ($("#autobet_lose_change_odds").is(":checked")) {
                autobet_lose_change_odds = $("#autobet_lose_change_odds_value").val();
            }
            if ($("#autobet_change_client_seed").is(":checked")) {
                change_client_seed = 1;
            }
            if ($("#autobet_max_bet_reset").is(":checked")) {
                reset_after_max_bet = 1;
            }
            if ($("#autobet_dnr").is(":checked")) {
                autobet_dnr = true;
            }
            if ($("#stop_after_profit").is(":checked")) {
                stop_after_profit = $("#stop_after_profit_value").val();
            }
            if ($("#stop_after_loss").is(":checked")) {
                stop_after_loss = $("#stop_after_loss_value").val();
            }
            if ($("#autobet_log_bets").is(":checked")) {
                logging = 1;
                $("#autobet_view_bet_log").show();
            }
            if ($("#autobet_enable_worker").is(":checked")) {
                enable_worker = 1;
            }
            if ($("#autobet_log_bets").is(":checked")) {
                logging = 1;
                $(".autobet_view_bet_log").show();
            }
            if ($("#autobet_enable_sounds").is(":checked")) {
                enable_sounds = 1;
            }
            autobet_running = true;
            autobet_history = [];
            AutoBet(mode, bet_count, max_bet, base_bet, autobet_win_return_to_base, autobet_lose_return_to_base, autobet_win_increase_bet_percent, autobet_lose_increase_bet_percent, change_client_seed, reset_after_max_bet, rolls_played, biggest_bet, biggest_win, session_pl, autobet_win_change_odds, autobet_lose_change_odds, stop_after_profit, stop_after_loss, logging, enable_sounds);
        }
    });
    $("#stop_autobet_button").click(function(event) {
        stop_autobet = true;
    });
    var free_play_sound_cookie = $.cookie('free_play_sound');
    if (free_play_sound_cookie == 1) {
        $("#free_play_sound").prop("checked", true);
        free_play_sound = true;
    }
    $.ionSound({
        sounds: ["jump_up", "bell_ring", "tap"],
        path: "https://fbtc-audio.freebitco.in/",
        multiPlay: true
    });
    $("#test_sound").click(function(event) {
        $.ionSound.play("jump_up");
    });
    $("#free_play_sound").click(function(event) {
        $.cookie.raw = true;
        if ($("#free_play_sound").is(":checked")) {
            $.cookie('free_play_sound', 1, {
                expires: 3650,
                secure: true
            });
            free_play_sound = true;
        } else {
            $.cookie('free_play_sound', 0, {
                expires: 3650,
                secure: true
            });
            free_play_sound = false;
        }
    });
    $("#auto_withdraw_option_link").click(function(event) {
        $(".withdraw_options").hide();
        $("#auto_withdraw_option").show();
    });
    $("#manual_withdraw_option_link").click(function(event) {
        $(".withdraw_options").hide();
        $("#manual_withdraw_option").show();
    });
    $("#instant_withdraw_option_link").click(function(event) {
        $(".withdraw_options").hide();
        $("#instant_withdraw_option").show();
    });
    $("#bcc_withdraw_option_link").click(function(event) {
        $(".withdraw_options").hide();
        $("#bcc_withdraw_option").show();
    });
    $(".withdraw_options_ul a").click(function() {
        $(".withdraw_options_ul a.active").removeClass();
        $(this).addClass('active').blur();
        return false;
    });
    $("#auto_withdraw_option_link").click();
    $(".remove_autobet_error").focus(function() {
        $("#autobet_error").hide();
        $("#autobet_error").html('');
    });
    $("#instant_withdrawal_button").click(function(event) {
        $("#instant_withdrawal_button").attr("disabled", true);
        var posting = $.post('/', {
            op: 'withdraw',
            type: 'instant',
            amount: $("#instant_withdrawal_amount").val(),
            withdraw_address: $("#instant_withdraw_btc_add").val(),
            tfa_code: $("#iw_tfa_code").val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            if (result[0] == "s") {
                $('#balance').html(result[2]);
                balanceChanged();
                $('#manual_withdraw_btc_add').val('');
                $('#instant_withdraw_btc_add').val('');
                withdraw_max_amount = parseFloat(Math.floor((result[2] - 0.00000001) * 100000000) / 100000000).toFixed(8);
                $("#withdrawal_amount").val('');
                $("#instant_withdrawal_amount").val('');
                $("#manual_withdraw_amt_recv").html('0.00000000');
                $("#instant_withdraw_amt_recv").html('0.00000000');
            }
            DisplaySEMessage(result[0], result[1]);
            $("#instant_withdrawal_button").attr("disabled", false);
        });
    });
    $("#withdrawal_button").click(function(event) {
        $("#withdrawal_button").attr("disabled", true);
        var posting = $.post('/', {
            op: 'withdraw',
            type: 'slow',
            amount: $("#withdrawal_amount").val(),
            withdraw_address: $("#manual_withdraw_btc_add").val(),
            tfa_code: $("#mw_tfa_code").val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            if (result[0] == "s") {
                $('#balance').html(result[2]);
                balanceChanged();
                $('#manual_withdraw_btc_add').val('');
                $('#instant_withdraw_btc_add').val('');
                withdraw_max_amount = parseFloat(Math.floor((result[2] - 0.00000001) * 100000000) / 100000000).toFixed(8);
                $("#withdrawal_amount").val('');
                $("#instant_withdrawal_amount").val('');
                $("#manual_withdraw_amt_recv").html('0.00000000');
                $("#instant_withdraw_amt_recv").html('0.00000000');
            }
            DisplaySEMessage(result[0], result[1]);
            $("#withdrawal_button").attr("disabled", false);
        });
    });
    $("#exchange_bcc_button").click(function(event) {
        $("#exchange_bcc_button").attr("disabled", true);
        var posting = $.post('/', {
            op: 'exchange_bcc'
        });
        posting.done(function(data) {
            var result = data.split(":");
            if (result[0] == "s") {
                $('#balance').html(result[2]);
                balanceChanged();
                $('#exchange_bcc_link').hide();
            }
            DisplaySEMessage(result[0], result[1]);
            $("#exchange_bcc_button").attr("disabled", false);
        });
    });
    $("#main_deposit_address_qr_code_link").click(function() {
        $("#main_deposit_address_qr_code").show();
    });
    $(".logout_link").click(function(event) {
        $.cookie.raw = true;
        $.removeCookie('btc_address');
        $.removeCookie('password');
        window.location.replace("https://freebitco.in/");
    });
    $("#hide_enable_2fa_msg_alert").click(function() {
        $('#enable_2fa_msg_alert').hide();
        $.cookie.raw = true;
        $.cookie('hide_enable_2fa_msg_alert', 1, {
            expires: 7,
            secure: true
        });
    });
    $("#hide_earn_btc_msg").click(function() {
        $('#earn_btc_msg').hide();
        $.cookie.raw = true;
        $.cookie('hide_earn_btc_msg', 1, {
            expires: 3650,
            secure: true
        });
    });
    $("#double_your_btc_tab").on('keydown', '#double_your_btc_stake', function(e) {
        var keyCode = e.keyCode || e.which;
        if (keyCode == 72) {
            e.preventDefault();
            if ($("#double_your_btc_bet_hi_button").is(":enabled")) {
                $("#double_your_btc_bet_hi_button").click();
            }
        } else if (keyCode == 76) {
            e.preventDefault();
            if ($("#double_your_btc_bet_lo_button").is(":enabled")) {
                $("#double_your_btc_bet_lo_button").click();
            }
        } else if (keyCode == 65) {
            e.preventDefault();
            if ($("#double_your_btc_stake").val() > 0.00000001) {
                $("#double_your_btc_half").click();
            }
        } else if (keyCode == 83) {
            e.preventDefault();
            $("#double_your_btc_2x").click();
        } else if (keyCode == 68) {
            e.preventDefault();
            $("#double_your_btc_min").click();
        } else if (keyCode == 70) {
            e.preventDefault();
            $("#double_your_btc_max").click();
        } else if (keyCode == 81) {
            e.preventDefault();
            $("#double_your_btc_payout_multiplier").val(parseFloat($("#double_your_btc_payout_multiplier").val()) + 1);
            $("#double_your_btc_payout_multiplier").keyup();
        } else if (keyCode == 87) {
            e.preventDefault();
            $("#double_your_btc_payout_multiplier").val(parseFloat($("#double_your_btc_payout_multiplier").val()) - 1);
            $("#double_your_btc_payout_multiplier").keyup();
        } else if (keyCode == 69) {
            e.preventDefault();
            $("#double_your_btc_payout_multiplier").val(parseFloat($("#double_your_btc_payout_multiplier").val()) + 5);
            $("#double_your_btc_payout_multiplier").keyup();
        } else if (keyCode == 82) {
            e.preventDefault();
            $("#double_your_btc_payout_multiplier").val(parseFloat($("#double_your_btc_payout_multiplier").val()) - 5);
            $("#double_your_btc_payout_multiplier").keyup();
        } else if (keyCode == 84) {
            e.preventDefault();
            $("#double_your_btc_payout_multiplier").val(parseFloat($("#double_your_btc_payout_multiplier").val()) + 10);
            $("#double_your_btc_payout_multiplier").keyup();
        } else if (keyCode == 89) {
            e.preventDefault();
            $("#double_your_btc_payout_multiplier").val(parseFloat($("#double_your_btc_payout_multiplier").val()) - 10);
            $("#double_your_btc_payout_multiplier").keyup();
        }
    });
    $("#lottery_tickets_purchase_count").keyup(function(event) {
        var lottery_tickets_purchase_count = parseInt($("#lottery_tickets_purchase_count").val());
        var lottery_ticket_price = parseFloat($(".lottery_ticket_price").html()).toFixed(8);
        $("#lottery_total_purchase_price").html(parseFloat(lottery_tickets_purchase_count * lottery_ticket_price * 100000000 / 100000000).toFixed(8));
    });
    $("#lottery_tickets_purchase_count").keypress(function() {
        $("#lottery_tickets_purchase_count").keyup();
    });
    $("#lottery_tickets_purchase_count").keydown(function() {
        $("#lottery_tickets_purchase_count").keyup();
    });
    $("#purchase_lottery_tickets_button").click(function(event) {
        $("#purchase_lottery_tickets_button").attr("disabled", true);
        $.get('/?op=purchase_lott_tickets&num=' + $("#lottery_tickets_purchase_count").val(), function(data) {
            var result = data.split(":");
            $('#lottery_tickets_purchase_message').html("");
            $('#lottery_tickets_purchase_message').show();
            $('#lottery_tickets_purchase_message').removeClass('free_play_result_error');
            $('#lottery_tickets_purchase_message').removeClass('free_play_result_success');
            if (result[0] == "e") {
                $('#lottery_tickets_purchase_message').addClass('free_play_result_error');
                $('#lottery_tickets_purchase_message').html(result[1]);
            }
            if (result[0] == "s") {
                $('#lottery_tickets_purchase_message').addClass('free_play_result_success');
                if (result[1] == "s1") {
                    var ticket_word = "tickets";
                    if (parseInt(result[2]) == 1) {
                        ticket_word = "ticket";
                    }
                    $('#lottery_tickets_purchase_message').html("Successfully purchased " + result[2] + " " + ticket_word + " in lottery round " + result[5] + " for " + parseFloat(result[4] / 100000000).toFixed(8) + " BTC.");
                    $('#user_lottery_tickets').html(ReplaceNumberWithCommas(result[3]));
                    $('#balance').html(parseFloat(result[6] / 100000000).toFixed(8));
                    balanceChanged();
                }
            }
            $("#purchase_lottery_tickets_button").attr("disabled", false);
        });
    });
    var free_play_claim_amount = 0;
    $(window).scroll(function() {
        $('.fbtc_left_sky').css('top', $(this).scrollTop());
    });
    $("#free_play_claim_button").click(function(event) {
        window.location.href = 'https://freebitco.in/?op=home&free_play_claim=' + free_play_claim_amount;
    });
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
    var lottery_winners_start = 1;
    var lottery_show_older = 0;
    var lottery_show_newer = 0;
    $("#older_lottery_winners_link").click(function() {
        lottery_show_older = 1;
    });
    $("#newer_lottery_winners_link").click(function() {
        lottery_show_newer = 1;
    });
    $(".browse_lottery_winners_link").click(function() {
        if (lottery_show_older == 1) {
            lottery_winners_start = lottery_winners_start + 1;
        } else if (lottery_show_newer == 1) {
            lottery_winners_start = lottery_winners_start - 1;
        }
        if (lottery_winners_start >= latest_lottery_round - 1) {
            lottery_winners_start = latest_lottery_round - 1;
        }
        if (lottery_show_older == 1 && lottery_winners_start < 1) {
            lottery_winners_start = 1;
        }
        $("#older_lottery_winners_link").attr("disabled", true);
        $("#newer_lottery_winners_link").attr("disabled", true);
        $.get('/stats_new_public/?f=lottery_winners&start=' + lottery_winners_start, function(data) {
            if (parseInt(data.round) > 0) {
                $("#previous_lottery_winners_list_div").html("");
                var mobile_class = "";
                if (mobile_device == 1) {
                    mobile_class = " lottery_table_mobile_style ";
                }
                $("#previous_lottery_winners_list_div").html('<div class="large-12 small-12 columns center lottery_winner_table_box table_header_background br_5_5"><div class="center" style="margin:auto;">LOTTERY ROUND ' + data.round + '</div></div><div class="large-12 small-12 columns center lottery_winner_table_box"><div class="center" style="margin:auto; font-weight:bold;">TOTAL TICKETS: ' + data.total_tickets + '</div></div><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="font_bold large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">#</div><div class="font_bold large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell' + mobile_class + '">USER ID</div><div class="font_bold large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell' + mobile_class + '">AMOUNT WON</div><div class="font_bold large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">USER TICKETS</div></div>');
                for (var i = 0; i < data.winners.length; i++) {
                    $("#previous_lottery_winners_list_div").append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + data.winners[i].rank + '</div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell' + mobile_class + '">' + data.winners[i].userid + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell' + mobile_class + '">' + data.winners[i].amount + ' BTC</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + data.winners[i].tickets_purchased + '</div> </div>');
                }
            }
            lottery_show_older = 0;
            lottery_show_newer = 0;
            $("#older_lottery_winners_link").attr("disabled", false);
            $("#newer_lottery_winners_link").attr("disabled", false);
        });
    });
    $("#newer_lottery_winners_link").click();
    $('.top-bar-section ul.right li').click(function() {
        $('.top-bar').removeClass('expanded');
    });
    $("#set_email_preferences").click(function() {
        $("#set_email_preferences").attr("disabled", true);
        var subs_arr = $('.email_subs_checkbox:checkbox:checked').map(function() {
            return this.value;
        }).get().toString();
        $.get('/?op=set_email_subscriptions&subs=' + subs_arr, function(data) {
            $("#set_email_preferences").attr("disabled", false);
            DisplaySEMessage('s', 'Succesfully updated email subscriptions');
        });
    });
    $("#double_your_btc_payout_multiplier").keyup(function() {
        if (parseFloat($("#double_your_btc_payout_multiplier").val()) < 1.01 && parseFloat($("#double_your_btc_payout_multiplier").val()) != 1) {
            $("#double_your_btc_payout_multiplier").val(1.01);
        } else if (parseFloat($("#double_your_btc_payout_multiplier").val()) > 4750.00) {
            $("#double_your_btc_payout_multiplier").val(4750.00);
        }
        CalculateWinAmount();
        $("#double_your_btc_win_chance").val((parseFloat(parseInt($(".lt").html()) / 10000 * 100).toFixed(2)) + "%");
    });
    $("#double_your_btc_win_chance").keyup(function() {
        if (parseFloat($("#double_your_btc_win_chance").val()) > 94.06) {
            $("#double_your_btc_win_chance").val(94.06 + "%");
        } else if (parseFloat($("#double_your_btc_win_chance").val()) < 0.02 && parseFloat($("#double_your_btc_win_chance").val()) != 0) {
            $("#double_your_btc_win_chance").val(0.02 + "%");
        }
        $("#double_your_btc_payout_multiplier").val(parseFloat(95 / parseFloat($("#double_your_btc_win_chance").val())).toFixed(2));
        CalculateWinAmount();
    });
    $("#double_your_btc_payout_multiplier").change(function() {
        $("#double_your_btc_payout_multiplier").val(parseFloat(9500 / parseInt($(".lt").html())).toFixed(2));
    });
    $("#double_your_btc_win_chance").change(function() {
        $("#double_your_btc_win_chance").val((parseFloat(parseInt($(".lt").html()) / 10000 * 100).toFixed(2)) + "%");
    });
    $("#double_your_btc_win_chance").focus(function() {
        $("#win_chance_value_message").show();
    });
    $("#double_your_btc_win_chance").focusout(function() {
        $("#win_chance_value_message").hide();
    });
    $("#double_your_btc_payout_multiplier").focus(function() {
        $("#payout_value_message").show();
    });
    $("#double_your_btc_payout_multiplier").focusout(function() {
        $("#payout_value_message").hide();
    });
    $('#login_form').hide();
    $('body').on('click', '.login_menu_button', function() {
        $('#signup_form').hide();
        $('#homepage_login_button').hide();
        $('#homepage_signup_button').show();
        $('#login_form').fadeIn();
    });
    $('body').on('click', '.signup_menu_button', function() {
        $('#login_form').hide();
        $('#homepage_login_button').show();
        $('#homepage_signup_button').hide();
        $('#signup_form').fadeIn();
    });
    $("#link_features").click(function() {
        $('html, body').animate({
            scrollTop: $("#features").offset().top - 40
        }, 800);
    });
    $("#link_home, .login_menu_button, .signup_menu_button").click(function() {
        $('html, body').animate({
            scrollTop: $("#new_home").offset().top - 40
        }, 800);
    });
    $("#link_bitcoin").click(function() {
        $('html, body').animate({
            scrollTop: $("#home_bitcoin").offset().top - 40
        }, 800);
    });
    $("#link_news").click(function() {
        $('html, body').animate({
            scrollTop: $("#home_news").offset().top - 40
        }, 800);
    });
    var have_account_cookie = $.cookie('have_account');
    if (have_account_cookie == 1) {
        $(".login_menu_button").click();
    }
    if (document.createElement("input").placeholder == undefined) {
        $(".form_placeholders").show();
    }
    $("#user_ads_unselect_all_countries").click(function(event) {
        $("#user_ads_target_country_code option").prop("selected", false);
    });
    $("#ad_details_unselect_all_countries").click(function(event) {
        $("#ad_details_target_country_code option").prop("selected", false);
    });
    $(".ad_position_checkbox").click(function(event) {
        $(".ad_position_checkbox").prop("checked", false);
        $(".ad_position_checkbox:checkbox[value=" + this.value + "]").prop("checked", true);
    });
    $("#autobet_bet_odds").focus(function() {
        $("#autobet_payout_value_message").show();
    });
    $("#autobet_bet_odds").focusout(function() {
        $("#autobet_payout_value_message").hide();
    });
    var auto_to_manual = 0;
    $('#auto_bet_on').click(function() {
        $('#double_your_btc_result').hide();
        $('#double_your_btc_left_section').hide();
        $('#double_your_btc_auto_bet_left_section').show();
        $('#disable_animation').hide();
        $('#double_your_btc_right_section').hide();
        $('#double_your_btc_auto_bet_right_section').show();
        $('.manual_bet_element').hide();
        $('.auto_bet_element').show();
        $("#double_your_btc_middle_section").css({
            'height': 'auto',
            'padding-bottom': '6px'
        });
        $(this).addClass('betting_mode_on');
        $(this).removeClass('manual_auto_bet_on_button');
        $('#manual_bet_on').removeClass('betting_mode_on');
        $('#manual_bet_on').addClass('manual_auto_bet_on_button');
        $('#multiplier_first_digit').html('0');
        $('#multiplier_second_digit').html('0');
        $('#multiplier_third_digit').html('0');
        $('#multiplier_fourth_digit').html('0');
        $('#multiplier_fifth_digit').html('0');
        $('#multiplier_enable_sound_div').hide();
        auto_to_manual = 1;
    });
    $('#manual_bet_on').click(function() {
        if (autobet_running === true) {
            $('#auto_bet_running').show();
        } else {
            if (auto_to_manual === 1 && $('body').innerWidth() > 1255) {
                auto_to_manual = 0;
                $("#double_your_btc_middle_section").css({
                    'border-radius': '0'
                });
            }
            $('#autobet_results_box').hide();
            $('#disable_animation').show();
            $('#double_your_btc_result').hide();
            $('#double_your_btc_auto_bet_left_section').hide();
            $('#double_your_btc_left_section').show();
            $('#double_your_btc_auto_bet_right_section').hide();
            $('#double_your_btc_right_section').show();
            $('.auto_bet_element').hide();
            $('.manual_bet_element').show();
            $("#double_your_btc_middle_section").css({
                'height': '362.781px'
            });
            $(this).addClass('betting_mode_on');
            $(this).removeClass('manual_auto_bet_on_button');
            $('#auto_bet_on').removeClass('betting_mode_on');
            $('#auto_bet_on').addClass('manual_auto_bet_on_button');
            $('#multiplier_first_digit').html('0');
            $('#multiplier_second_digit').html('0');
            $('#multiplier_third_digit').html('0');
            $('#multiplier_fourth_digit').html('0');
            $('#multiplier_fifth_digit').html('0');
            $('#multiplier_enable_sound_div').show();
            $("#autobet_error").hide();
        }
    });
    $('#close_auto_bet_running_message').click(function() {
        $('#auto_bet_running').hide();
    });
    $('#show_double_your_btc_auto_bet_on_lose').click(function() {
        $(this).addClass('multiplier_header_background');
        $('#show_double_your_btc_auto_bet_on_win').removeClass('multiplier_header_background');
        $('#double_your_btc_auto_bet_on_win').hide();
        $('#double_your_btc_auto_bet_on_lose').show();
    });
    $('#show_double_your_btc_auto_bet_on_win').click(function() {
        $(this).addClass('multiplier_header_background');
        $('#show_double_your_btc_auto_bet_on_lose').removeClass('multiplier_header_background');
        $('#double_your_btc_auto_bet_on_lose').hide();
        $('#double_your_btc_auto_bet_on_win').show();
    });
    $('#manual_bet_on').click();
    $("#home_news").find(".news_content:first").show();
    $("#news_tab").find(".inside_news_content:first").show();
    $('#newer_bet_history').click(function() {
        bet_history_page--;
        if (bet_history_page < 0) {
            bet_history_page = 0;
        }
        GetBetHistory(bet_history_page);
    });
    $('#older_bet_history').click(function() {
        bet_history_page++;
        if (bet_history_page < 0) {
            bet_history_page = 0;
        }
        GetBetHistory(bet_history_page);
    });
    $('#show_roll_history_mobile').click(function() {
        $('#bet_history_table').toggle();
    });
    $("#signup_page_captcha_types").change(function() {
        $('.signup_page_captcha').hide();
        $('#' + $("#signup_page_captcha_types").val() + '_captcha').show();
    });
    $(".reward_point_redeem_result_box_close").click(function() {
        $('#reward_point_redeem_result_container_div').hide();
    });
    $("#encash_points_number").keyup(function(event) {
        var encash_points_number = parseInt($("#encash_points_number").val());
        var lottery_ticket_price = parseFloat($(".lottery_ticket_price").html()).toFixed(8);
        $("#reward_points_redeem_price").html(parseFloat(encash_points_number * lottery_ticket_price * 100000000 / 100000000).toFixed(8));
    });
    $("#encash_points_number").keypress(function() {
        $("#encash_points_number").keyup();
    });
    $("#encash_points_number").keydown(function() {
        $("#encash_points_number").keyup();
    });
    $(".reward_category_name").click(function() {
        if ($(this).find('.toggle_down_up').prop('className').split(' ').indexOf("fa-arrow-down") > -1) {
            $(this).find('.toggle_down_up').remove();
            $(this).append('<i class="toggle_down_up fa fa-arrow-up" style="float:right; color: #fff;" aria-hidden="true"></i>');
        } else if ($(this).find('.toggle_down_up').prop('className').split(' ').indexOf("fa-arrow-up") > -1) {
            $(this).find('.toggle_down_up').remove();
            $(this).append('<i class="toggle_down_up fa fa-arrow-down" style="float:right; color: #fff;" aria-hidden="true"></i>');
        }
        $(this).next(".reward_category_details").slideToggle("200");
        $(this).next(".profile_change_box").slideToggle("200");
    });
    $('#disable_lottery_checkbox').click(function() {
        var val = 0;
        if ($("#disable_lottery_checkbox").is(":checked")) {
            val = 1;
        }
        $.get('/?op=toggle_lottery&value=' + val, function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
        });
    });
    $('#disable_interest_checkbox').click(function() {
        var val = 0;
        if ($("#disable_interest_checkbox").is(":checked")) {
            val = 1;
        }
        $.get('/?op=toggle_interest&value=' + val, function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
        });
    });
    $("#enable_2fa_0").click(function() {
        $.get('/?op=confirm_email', function(data) {
            var result = data.split(":");
            var custom_timeout = 0;
            if (result[2] == "s2") {
                $("#enable_2fa_0").parent("div").hide();
                $("#enable_2fa_2").parent("div").show();
            }
            if (result[0] == "s") {
                custom_timeout = 30000;
            }
            DisplaySEMessage(result[0], result[1], custom_timeout);
        });
    });
    $("#enable_2fa_2").click(function() {
        $.get('/?op=enable_2fa&func=show', function(data) {
            var result = data.split(":");
            if (result[0] == "s") {
                $("#enable_2fa_2").parent("div").hide();
                $("#show_2fa_secret").show();
                var totp = "otpauth://totp/" + result[2] + "?secret=" + result[3];
                $("#2fa_secret").html("<p id='tfa_secret_qr_code'></p><p style='height: 45px; margin-right: auto; margin-left: auto; width: 300px; border-radius: 3px;'><span class='secret_key_background left'>Secret Key </span><span class='left bold' style='width: 180px; padding: 10px; border: 1px solid #ccc; border-left: none; border-radius: 0 3px 3px 0;'>" + result[3] + "</span></p>");
                $('#tfa_secret_qr_code').qrcode({
                    width: 200,
                    height: 200,
                    text: totp
                });
            } else {
                DisplaySEMessage(result[0], result[1]);
            }
        });
    });
    $("#activate_2fa").click(function() {
        var posting = $.post('/', {
            op: 'enable_2fa',
            func: 'enable',
            code: $("#activate_2fa_code").val(),
            phone: $("#tfa_recovery_phone").val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            if (result[0] == "s") {
                $("#activate_2fa").parent("div").hide();
                $("#disable_2fa").parent("div").show();
                $(".profile_2fa_field").show();
                $("html, body").animate({
                    scrollTop: $("#2fa_profile_box").offset().top - 45
                }, "fast");
                $('#enable_2fa_msg_alert').hide();
            }
        });
    });
    $("#disable_2fa").click(function() {
        var posting = $.post('/', {
            op: 'enable_2fa',
            func: 'disable',
            code: $("#disable_2fa_code").val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
            if (result[0] == "s") {
                $("#enable_2fa_0").parent("div").show();
                $(".profile_2fa_field").hide();
                $("#disable_2fa").parent("div").hide();
            }
        });
    });
    $("#submit_2fa_recovery_details").click(function() {
        var posting = $.post('/', {
            op: 'change_2fa_phone',
            code: $("#rp_tfa_code").val(),
            phone: $("#rp_phone_number").val()
        });
        posting.done(function(data) {
            var result = data.split(":");
            DisplaySEMessage(result[0], result[1]);
        });
    });
    $(".tfa_enter_recovery_phone_link").click(function() {
        SwitchPageTabs('edit');
        $("html, body").animate({
            scrollTop: $("#2fa_recovery_phone_box").offset().top - 45
        }, "fast");
        $("#2fa_recovery_phone_box").click();
    });
    $("#share_button").click(function() {
        var amount = $('#share_amount').val();
        var conf = confirm("If you click OK, then " + amount + " BTC will be deducted from your account and distributed among your referrals. If you do not wish to do this, please click CANCEL");
        if (conf == true) {
            $("#share_button").attr("disabled", true);
            var method = 0;
            if ($("#equal_share").is(":checked")) {
                method = 1;
            }
            if ($("#weighted_share").is(":checked")) {
                method = 2;
            }
            if ($("#last_payout_share").is(":checked")) {
                method = 3;
            }
            $.get('/?op=share_coins&method=' + method + '&amount=' + amount, function(data) {
                var result = data.split(":");
                if (result[0] == "e") {
                    DisplaySEMessage(result[0], result[1]);
                } else if (result[0] == "s") {
                    var share_amount = parseFloat(parseInt(result[2]) / 100000000).toFixed(8);
                    $('#balance').html(parseFloat(parseInt(result[1]) / 100000000).toFixed(8));
                    balanceChanged();
                    $('#share_given').html(result[4]);
                    $('#recent_share_given').html(result[5]);
                    DisplaySEMessage(result[0], share_amount + " BTC shared with " + result[3] + " referrals");
                }
                $("#share_button").attr("disabled", false);
            });
        }
    });
    $("#claim_bonus_button").click(function() {
        if ($("#accept_bonus_terms").is(":checked")) {
            $.get('/?op=credit_deposit_bonus&amount=' + $("#claim_bonus_amount").val(), function(data) {
                var result = data.split(":");
                if (result[0] == "e") {
                    DisplaySEMessage(result[0], result[1]);
                } else if (result[0] == "s") {
                    $('#bonus_account_table').show();
                    $('#user_claimed_deposit_bonus').show();
                    $('#bonus_account_balance').html(result[1] + " BTC");
                    $('#bonus_account_wager').html(result[2] + " BTC");
                    $('#balance').html(result[3]);
                    balanceChanged();
                    $('#bonus_eligible_msg').hide();
                    DisplaySEMessage(result[0], result[4]);
                    $("#myModal24").foundation('reveal', 'close');
                    bonus_table_closed = 0;
                }
            });
        } else {
            DisplaySEMessage("e", "Please read and agree to the terms below");
        }
    });
    $("#earn_btc_acc_balance").keyup(function(event) {
        var acc_bal = parseInt($("#earn_btc_acc_balance").val() * 100000000);
        if (acc_bal > 29000) {
            $("#earn_btc_daily_interest").html(parseFloat((Math.floor(acc_bal * 0.0109589 / 100)) / 100000000).toFixed(8));
            $("#earn_btc_monthly_interest").html(parseFloat((Math.floor(acc_bal * 0.32928995 / 100)) / 100000000).toFixed(8));
            $("#earn_btc_yearly_interest").html(parseFloat((Math.floor(acc_bal * 4.08050588 / 100)) / 100000000).toFixed(8));
        } else {
            $("#earn_btc_daily_interest").html("0");
            $("#earn_btc_monthly_interest").html("0");
            $("#earn_btc_yearly_interest").html("0");
        }
    });
    $("#earn_btc_acc_balance").keypress(function() {
        $("#earn_btc_acc_balance").keyup();
    });
    $("#earn_btc_acc_balance").keydown(function() {
        $("#earn_btc_acc_balance").keyup();
    });
    $("#earn_btc_acc_balance").val($('#balance').html());
    $("#earn_btc_acc_balance").keyup();
    $("#hide_pending_payouts_table").click(function() {
        hide_pending_payments = 1;
        $('#pending_payouts_table_new').hide();
    });
    $("#hide_pending_deposits_table").click(function() {
        hide_pending_deposits = 1;
        $('#unconfirmed_deposits_table').hide();
    });
    $("#claim_bonus_link").click(function() {
        if (max_deposit_bonus > parseFloat(min_bonus_amount)) {
            $('.dep_bonus_max').html(max_deposit_bonus + " BTC");
            if (max_deposit_bonus > parseFloat($('#balance').html())) {
                $('.dep_bonus_max').val($('#balance').html());
            } else {
                $('.dep_bonus_max').val(max_deposit_bonus);
            }
        }
        $('#balance2').html($('#balance').html());
    });
    $('#bet_history_table_rows').on('click', '.show_balance_before_after', function() {
        $(this).toggleClass('fa-arrows-alt fa-arrow-up');
        $(this).parents('.multiply_bet_history_table_row').children('.balance_before_after').toggle();
    });
    if ($.cookie('hide_no_apps_msg') != 1) {
        $('#no_apps_msg').show();
    }
    $("#hide_no_apps_msg").click(function() {
        $('#no_apps_msg').hide();
        $.cookie.raw = true;
        $.cookie('hide_no_apps_msg', 1, {
            expires: 3650,
            secure: true
        });
    });
    if ($.cookie('hide_mine_btc_msg') != 1) {
        $('#mine_btc_msg').show();
    }
    $("#exchange_btg_button").click(function(event) {
        $("#exchange_btg_button").attr("disabled", true);
        var posting = $.post('/', {
            op: 'exchange_btg'
        });
        posting.done(function(data) {
            var result = data.split(":");
            if (result[0] == "s") {
                $('#balance').html(result[2]);
                balanceChanged();
                $('#exchange_btg_link').hide();
            }
            DisplaySEMessage(result[0], result[1]);
            $("#exchange_btg_button").attr("disabled", false);
        });
    });
    if ($('body').innerWidth() < 763) {
        $('#deposit_withdraw_container').addClass('deposit_withdraw_container_mobile');
        $('#deposit_withdraw_container').removeClass('deposit_withdraw_container');
        $('#add_lottery_table_mobile_style').addClass('lottery_table_mobile_style');
        $('#captchasnet_captcha_info_span_mobile').addClass('captchasnet_captcha_info_span_mobile');
        $('.reward_table_box_left_size_change').addClass('reward_table_box_left_mobile');
        $('.reward_table_box_left_size_change').removeClass('reward_table_box_left');
        $('.reward_table_box_right_size_change').addClass('reward_table_box_right_mobile');
        $('.reward_table_box_right_size_change').removeClass('reward_table_box_right');
        $('#reward_table_box_left_size_change').addClass('border_bottom_none');
        $('#hide_show_roll_history_mobile').show();
        $('#bet_history_table').hide();
        $('#lottery_first_amount').addClass('br_0_5');
        $('lottery_second_third_div').removeClass('br_5_5');
        $('lottery_second_container_div').removeClass('br_right_1px');
        $('lottery_second_div').removeClass('br_5');
    } else {
        $('#deposit_withdraw_container').addClass('deposit_withdraw_container');
        $('#deposit_withdraw_container').removeClass('deposit_withdraw_container_mobile');
        $('#add_lottery_table_mobile_style').removeClass('lottery_table_mobile_style');
        $('#captchasnet_captcha_info_span_mobile').removeClass('captchasnet_captcha_info_span_mobile');
        $('.reward_table_box_left_size_change').addClass('reward_table_box_left');
        $('.reward_table_box_left_size_change').removeClass('reward_table_box_left_mobile');
        $('.reward_table_box_right_size_change').addClass('reward_table_box_right');
        $('.reward_table_box_right_size_change').removeClass('reward_table_box_right_mobile');
        $('#reward_table_box_left_size_change').removeClass('border_bottom_none');
        $('#hide_show_roll_history_mobile').hide();
        $('#bet_history_table').show();
        $('#lottery_first_amount').removeClass('br_0_5');
        $('lottery_second_third_div').addClass('br_5_5');
        $('lottery_second_container_div').removeClass('br_right_1px');
        $('lottery_second_div').removeClass('br_5');
    }
    $("#play_without_captchas_button").click(function() {
        $('#free_play_captcha_container').hide();
        $('#play_without_captchas_button').hide();
        $('#play_with_captcha_button').show();
        $('#play_without_captcha_desc').show();
        $('#pwc_input').val("1");
    });
    $("#play_with_captcha_button").click(function() {
        $('#free_play_captcha_container').show();
        $('#play_without_captchas_button').show();
        $('#play_with_captcha_button').hide();
        $('#play_without_captcha_desc').hide();
        $('#pwc_input').val("0");
    });
    $("#older_wagering_contest_winners_link").click(function() {
        if (wagering_contest_winners_round_display > 1) {
            wagering_contest_winners_round_display = wagering_contest_winners_round_display - 1;
            PreviousContestWinners(wagering_contest_winners_round_display);
            $('#wager_contest_round_display').html(wagering_contest_winners_round_display);
        }
    });
    $("#newer_wagering_contest_winners_link").click(function() {
        if (wagering_contest_winners_round_display < current_contest_round - 1) {
            wagering_contest_winners_round_display = wagering_contest_winners_round_display + 1;
            PreviousContestWinners(wagering_contest_winners_round_display);
            $('#wager_contest_round_display').html(wagering_contest_winners_round_display);
        }
    });
    $(".play_jackpot").prop("checked", false);
    $(".autobet_play_jackpot").prop("checked", false);
    $(".low_balance_buy_btc").click(function() {
        $('#main_deposit_button_top').click();
    });
    $(".low_balance_deposit_btc").click(function() {
        $('#main_deposit_button_top').click();
    });
    $("#buy_bitcoins_button").click(function() {});
    $.cookie.raw = true;
    var hide_push_msg = $.cookie('hide_push_msg');
    if (hide_push_msg != 1) {
        pushpad('status', function(isSubscribed) {
            if (!isSubscribed) {
                $('.show_push_notifications_modal').click();
            }
        });
    }
    $('.pushpad_allow_button').on('click', function() {
        $("#push_notification_modal").foundation('reveal', 'close');
        pushpad('subscribe', function(isSubscribed) {
            if (isSubscribed) {
                $.cookie('hide_push_msg', 1, {
                    expires: 2,
                    secure: true
                });
            } else {
                alert("You have blocked the notifications from browser preferences: please update your browser preferences or click the lock near the address bar to change your notification preferences and then try again.");
            }
        });
    });
    $('.pushpad_deny_button').on('click', function() {
        $("#push_notification_modal").foundation('reveal', 'close');
        $.cookie('hide_push_msg', 1, {
            expires: 2,
            secure: true
        });
    });
    $("#multiply_now_div").click(function() {});
    $("#guest_user_wthdraw_button").click(function(event) {
        DisplaySEMessage('e', "You need to enter an email address and verify it before you can withdraw");
        SwitchPageTabs('edit');
        $("html, body").animate({
            scrollTop: $("#change_email_address_box").offset().top - 45
        }, "fast");
        $("#change_email_address_box").click();
    });
    $("#parimutuel_back_to_all_events_button").click(function() {
        $("#parimutuel_back_to_all_events_button_div").hide();
        $("#parimutuel_main_page_div").show();
        $("#parimutuel_game_container_page").hide();
        $("#parimutuel_page_main_text").show();
    });
    var cross_promo_msg_strings = ['<p class="bold inpage_promo_box" style="cursor:pointer;"><a href="javascript:void(0);" onclick="SwitchPageTabs(\x27double_your_btc\x27);">Why not try to multiply your bitcoins up to 4,750 times by playing a provably fair HI-LO game!</a></p>', '<p class="bold inpage_promo_box" style="cursor:pointer;"><a href="javascript:void(0);" onclick="SwitchPageTabs(\x27betting\x27);">Bet on the latest events and win big prizes!</a></p>'];
    var randomNumber = Math.floor(Math.random() * cross_promo_msg_strings.length);
    $('.cross_promo_msg_div').html(cross_promo_msg_strings[randomNumber]);
    $("#purchase_golden_lottery_tickets_button").click(function() {
        var num = parseInt($("#golden_lottery_tickets_purchase_count").val());
        $.get('/cgi-bin/api.pl?op=purchase_lambo_lott_tickets&num=' + num, function(data) {
            DisplaySEMessage(data.status, data.msg);
            if (data.status == "s") {
                $('#balance').html(parseFloat(data.balance / 100000000).toFixed(8));
                balanceChanged();
                $('#user_golden_lottery_tickets').html(ReplaceNumberWithCommas(data.user_tickets));
            }
        });
    });
    $("#golden_lottery_tickets_purchase_count").keyup(function(event) {
        var lottery_tickets_purchase_count = parseInt($("#golden_lottery_tickets_purchase_count").val());
        var lottery_ticket_price = 0.00025000;
        $("#golden_lottery_total_purchase_price").html(parseFloat(lottery_tickets_purchase_count * lottery_ticket_price * 100000000 / 100000000).toFixed(8));
    });
    $("#golden_lottery_tickets_purchase_count").keypress(function() {
        $("#golden_lottery_tickets_purchase_count").keyup();
    });
    $("#golden_lottery_tickets_purchase_count").keydown(function() {
        $("#golden_lottery_tickets_purchase_count").keyup();
    });
    if (userid > 0) {
        $.get('/stats_new_private/?u=' + socket_userid + '&p=' + socket_password + '&f=parimutuel_bet_history2', function(user_data) {
            if (user_data.status == "success") {
                parimutuel_bet_history_json = user_data;
            }
        });
    }
    if (captcha_type == 11) {
        var default_captcha = $.cookie('default_captcha');
        if (default_captcha === 'recaptcha' || default_captcha === 'double_captchas') {
            SwitchCaptchas(default_captcha);
        }
    }
    if ($('body').innerWidth() < 768) {
        $.cookie.raw = true;
        $.cookie('mobile', 1, {
            expires: 3650,
            secure: true
        });
        if (mobile_device != 1) {
            if (userid > 0) {
                window.location.reload();
            }
        }
    } else {
        $.cookie.raw = true;
        $.removeCookie('mobile');
    }
    if (userid > 0) {
        var tab = getParameterByName('tab');
        if (typeof tab != 'undefined') {
            $("#" + tab + "_link").click();
            $("." + tab + "_link").click();
            if (typeof tab != 'undefined' && tab == 'deposit_btc') {
                $("#main_deposit_button_top").click();
            }
        }
        var tab2 = getParameterByName('tab2');
        if (typeof tab2 != 'undefined' && tab2 != '' && tab2 != 0) {
            if (tab2 == "enable_2fa") {
                $("#enable_2fa_msg").click();
            }
        }
        if (free_play < 1) {
            if (multi_acct_same_ip > 0) {
                $('#multi_acct_same_ip').show();
            }
        }
        $("#mob_ver_country_code").find("#" + country + "_dcode").attr("selected", true);
        if (show_sky == 1 && $('body').innerWidth() < 1201) {
            $("#free_play_tab").css({
                'margin-left': '50px'
            });
        }
        if (rp_promo_active != 0) {
            if (parseInt(rp_promo_start) > 0) {
                rp_promo_active = 2;
            } else if (parseInt(rp_promo_end) > 0) {
                rp_promo_active = 1;
            } else {
                rp_promo_active = 0;
            }
            if (rp_promo_active == 2) {
                $("#rp_promo_" + rp_promo_counter + "_" + rp_promo_active2 + "_text").html("<b>" + rp_multiplier + "x reward points (RP) promotion starts in <span id='bonus_span_rp_promo_" + rp_promo_counter + "_" + rp_promo_active2 + "'></span></b> (" + free_rp + " RP/free roll, " + ref_rp + " RP/referral free roll, " + multiply_rp + " RP/multiply roll).<BR>Follow us on <b><a href='https://twitter.com/freebitco' target=_blank>twitter</a></b> to be notified before the promotion starts!");
                BonusEndCountdown('rp_promo_' + rp_promo_counter + '_' + rp_promo_active2, parseInt(rp_promo_start));
            } else if (rp_promo_active == 1) {
                $("#rp_promo_" + rp_promo_counter + "_" + rp_promo_active2 + "_text").html("<b>" + rp_multiplier + "x reward points (RP) promotion currently running and ends in <span id='bonus_span_rp_promo_" + rp_promo_counter + "_" + rp_promo_active2 + "'></span></b> (" + free_rp + " RP/free roll, " + ref_rp + " RP/referral free roll, " + multiply_rp + " RP/multiply roll).<BR>Follow us on <b><a href='https://twitter.com/freebitco' target=_blank>twitter</a></b> to be notified in advance about our future promotions!");
                $(".multiply_rp_amount").html(multiply_rp);
                $(".free_rp_amount").html(free_rp);
                $(".ref_rp_amount").html(ref_rp);
                BonusEndCountdown('rp_promo_' + rp_promo_counter + '_' + rp_promo_active2, parseInt(rp_promo_end));
                $("#bonus_weekend_msg_div").show();
                $("#bonus_weekend_rp_multiplier").html(rp_multiplier);
            }
            if ($.cookie("rp_promo_" + rp_promo_counter + "_" + rp_promo_active2) != 1) {
                $("#rp_promo_" + rp_promo_counter + "_" + rp_promo_active2).show();
            }
            $("#hide_rp_promo_" + rp_promo_counter + "_" + rp_promo_active2).click(function() {
                $("#rp_promo_" + rp_promo_counter + "_" + rp_promo_active2).hide();
                $.cookie.raw = true;
                $.cookie('rp_promo_' + rp_promo_counter + '_' + rp_promo_active2, 1, {
                    expires: 3,
                    secure: true
                });
            });
        }
        if (dep_bonus_eligible == 1) {
            $('.dep_bonus_max').html(max_deposit_bonus + " BTC");
            $('#bonus_eligible_msg').show();
        } else if (dep_bonus_eligible == 2) {
            $('#bonus_not_eligible_msg').show();
        }
        if (auto_withdraw == 1) {
            $('#earn_btc_msg').show();
            $('#hide_earn_btc_msg').hide();
        } else if (auto_withdraw == 0) {
            if ($.cookie('hide_earn_btc_msg') != 1) {
                $('#earn_btc_msg').show();
            }
        }
        if (bonus_locked_balance > 0 || bonus_wagering_remaining > 0) {
            $('#bonus_account_table').show();
            $('#user_claimed_deposit_bonus').show();
            $('#bonus_account_balance').html(bonus_locked_balance + " BTC");
            $('#bonus_account_wager').html(bonus_wagering_remaining + " BTC");
        }
        if (show_2fa_msg == 1) {
            if ($.cookie('hide_enable_2fa_msg_alert') != 1) {
                $('#enable_2fa_msg_alert').show();
            }
        }
        ScreeSizeCSSChanges();
        $(window).resize(ScreeSizeCSSChanges);
        $("#withdraw_delay_message4").hide();
        $("#" + token_name).val(token1);
        if (mobile_device == 1) {
            $("#show_referrals_mobile").click(function() {
                $("#referral_list_table").show();
                $("#show_more_refs_options").show();
                $("#show_referrals_mobile").html("YOUR REFERRALS");
            });
        }
        $(".hide_menu").click(function() {
            $("#menu_drop").click();
        });
        $("#menu_drop").click(function() {
            $(".top-bar-section").hide();
        });
    } else {
        $("#signup_token").val(signup_token);
        GenerateCaptchasNetCaptcha('captchasnet_forgot_password_captcha', 0);
    }
});
function BetErrors(code) {
    if (code == "e1") {
        $('#double_your_btc_error').html("Insufficient balance to make this bet");
        $('#low_balance_modal_link').click();
    }
    if (code == "e2") {
        $('#double_your_btc_error').html("Bet amount cannot be less than 0.00000001 BTC");
    }
    if (code == "e3") {
        $('#double_your_btc_error').html("Bet amount cannot be empty");
    }
    if (code == "e4") {
        $('#double_your_btc_error').html("Invalid bet method");
    }
    if (code == "e5") {
        $('#double_your_btc_error').html("Bet amount cannot be more than " + max_win_amount + " BTC");
    }
    if (code == "e6") {
        $('#double_your_btc_error').html("Please reload the page.");
    }
    if (code == "e7") {
        $('#double_your_btc_error').html("Payout multiplier has to be between 2x and 4750x");
    }
    if (code == "e8") {
        $('#double_your_btc_error').html("Win amount cannot be more than " + max_win_amount + " BTC");
    }
    if (code == "e9") {
        $('#double_your_btc_error').html("Your balance is insufficient to make this bet and try to win the jackpot<BR>Please un-select the jackpot bet option and try again");
        $('#low_balance_modal_link').click();
    }
    if (code == "e10") {
        $('#double_your_btc_error').html("Client Seed is either empty or has invalid characters (only letters and numbers allowed).<BR>Please correct it by clicking on the PROVABLY FAIR link above.");
    }
    if (code == "e11") {
        $('#double_your_btc_error').html("Please wait for your previous bet to finish rolling.");
    }
    if (code == "e12") {
        $('#double_your_btc_error').html("Betting is disabled in your country.");
    }
    if (code == "e13") {
        $('#double_your_btc_error').html("Please deposit bitcoins first to make a bet using a multiplier over 100x.");
    }
}
function DoubleYourBTC(mode) {
    $('#double_your_btc_digits').show();
    var enable_animation = 1;
    if ($("#disable_animation_checkbox").is(":checked")) {
        enable_animation = 0;
    }
    var intervalID1;
    var intervalID2;
    var intervalID3;
    var intervalID4;
    var intervalID5;
    if (enable_animation == 1) {
        intervalID1 = setInterval(function() {
            $("#multiplier_first_digit").html(Math.floor(Math.random() * 10));
        }, 10);
        intervalID2 = setInterval(function() {
            $("#multiplier_second_digit").html(Math.floor(Math.random() * 10));
        }, 10);
        intervalID3 = setInterval(function() {
            $("#multiplier_third_digit").html(Math.floor(Math.random() * 10));
        }, 10);
        intervalID4 = setInterval(function() {
            $("#multiplier_fourth_digit").html(Math.floor(Math.random() * 10));
        }, 10);
        intervalID5 = setInterval(function() {
            $("#multiplier_fifth_digit").html(Math.floor(Math.random() * 10));
        }, 10);
    }
    $("#double_your_btc_bet_hi_button").attr("disabled", true);
    $("#double_your_btc_bet_lo_button").attr("disabled", true);
    var bet = $("#double_your_btc_stake").val();
    var jackpot = 0;
    var jackpot_arr = $('.play_jackpot:checkbox:checked').map(function() {
        return this.value;
    }).get();
    if (jackpot_arr.length > 0) {
        jackpot = jackpot_arr.toString();
    }
    var client_seed = $('#next_client_seed').val();
    $.get('/cgi-bin/bet.pl?m=' + mode + '&client_seed=' + client_seed + '&jackpot=' + jackpot + '&stake=' + bet + '&multiplier=' + $("#double_your_btc_payout_multiplier").val() + '&rand=' + Math.random(), function(data) {
        var result = data.split(":");
        $('#double_your_btc_error').html("");
        $('#double_your_btc_error').hide();
        $('#double_your_btc_stake').removeClass('input-error');
        $('#double_your_btc_bet_win').html("");
        $('#double_your_btc_bet_lose').html("");
        $('#double_your_btc_bet_win').hide();
        $('#double_your_btc_bet_lose').hide();
        $('#jackpot_message').removeClass('green');
        $('#jackpot_message').removeClass('red');
        $('#jackpot_message').html('');
        $('#jackpot_message').hide();
        $('#double_your_btc_result').show();
        if (result[0] == "s1") {
            var number = result[2];
            var single_digit = number.split("");
            if (number.toString().length < 5) {
                var remaining = 5 - number.toString().length;
                for (var i = 0; i < remaining; i++) {
                    single_digit.unshift('0');
                }
            }
            if (enable_animation == 1) {
                clearInterval(intervalID1);
                clearInterval(intervalID2);
                clearInterval(intervalID3);
                clearInterval(intervalID4);
                clearInterval(intervalID5);
            }
            $("#multiplier_first_digit").html(single_digit[0]);
            $("#multiplier_second_digit").html(single_digit[1]);
            $("#multiplier_third_digit").html(single_digit[2]);
            $("#multiplier_fourth_digit").html(single_digit[3]);
            $("#multiplier_fifth_digit").html(single_digit[4]);
            $('#balance').html(result[3]);
            max_deposit_bonus = parseFloat(result[18]).toFixed(8);
            balanceChanged();
            $('#balance_usd').html(result[5]);
            $('#next_server_seed_hash').val(result[6]);
            $('#next_nonce').html(result[8]);
            $('.previous_server_seed').html(result[9]);
            $('.previous_server_seed').val(result[9]);
            $('#previous_server_seed_hash').val(result[10]);
            $('.previous_client_seed').html(result[11]);
            $('.previous_client_seed').val(result[11]);
            $('.previous_nonce').html(result[12]);
            $('#previous_roll').html(result[2]);
            $('#no_previous_rolls_msg').hide();
            $('#previous_rolls_table').show();
            $('#previous_roll_strings').show();
            $('#bonus_account_balance').html(result[16] + " BTC");
            $('#bonus_account_wager').html(result[17] + " BTC");
            if ((parseFloat(result[16]) <= 0 || parseFloat(result[17]) <= 0) && bonus_table_closed == 0) {
                setTimeout(function() {
                    $('#bonus_account_table').hide();
                    $('#user_claimed_deposit_bonus').hide();
                    bonus_table_closed = 1;
                }, 5000);
            }
            if (max_deposit_bonus >= parseFloat(min_bonus_amount) && bonus_table_closed == 1) {
                $('#bonus_eligible_msg').show();
            }
            if (parseFloat(result[19]) > 0 && parseFloat(result[19]) < 100) {
                $('.multiply_max_bet').html(result[19] + " BTC");
                $('.multiply_max_bet').val(result[19]);
                max_win_amount = parseFloat(result[19]);
            }
            $("#verify_rolls_link").attr("href", "https://s3.amazonaws.com/roll-verifier/verify.html?server_seed=" + result[9] + "&client_seed=" + result[11] + "&server_seed_hash=" + result[10] + "&nonce=" + result[12]);
            var capsmode = mode.toUpperCase();
            if (result[1] == "w") {
                $('#double_your_btc_bet_win').show();
                $('#double_your_btc_bet_win').html("You BET " + capsmode + " so you win " + result[4] + " BTC!");
                if ($("#manual_enable_sounds").is(":checked")) {
                    $.ionSound.play("bell_ring");
                }
            }
            if (result[1] == "l") {
                $('#double_your_btc_bet_lose').show();
                $('#double_your_btc_bet_lose').html("You BET " + capsmode + " so you lose " + result[4] + " BTC");
                if ($("#manual_enable_sounds").is(":checked")) {
                    $.ionSound.play("tap");
                }
            }
            if (jackpot != 0) {
                $('#jackpot_message').show();
                if (result[13] == "1") {
                    $('#jackpot_message').addClass('green');
                    $('#jackpot_message').html("Congratulations! You have won the jackpot of " + result[15] + " BTC");
                } else {
                    $('#jackpot_message').addClass('red');
                    $('#jackpot_message').html("Sorry, you did not win the jackpot.");
                }
            }
            var current_balance = parseFloat($('#balance').html());
            var bonus_account_balance = parseFloat($('#bonus_account_balance').html());
            if (current_balance + bonus_account_balance < 0.00000002) {
                $('#low_balance_modal_link').click();
            }
            $("#double_your_btc_bet_hi_button").attr("disabled", false);
            $("#double_your_btc_bet_lo_button").attr("disabled", false);
            insertIntoBetHistory(result[1], result[4], result[2], result[9], result[11], result[10], result[12], "DICE", mode, jackpot, bet, $("#double_your_btc_payout_multiplier").val(), result[20], result[21], result[22], result[23]);
        } else {
            $('#double_your_btc_error').show();
            $('#double_your_btc_digits').hide();
            if (parseFloat(result[1]) > 0 && parseFloat(result[1]) < 100) {
                $('.multiply_max_bet').html(result[1] + " BTC");
                $('.multiply_max_bet').val(result[1]);
                max_win_amount = parseFloat(result[1]);
            }
            BetErrors(result[0]);
            if (enable_animation == 1) {
                clearInterval(intervalID1);
                clearInterval(intervalID2);
                clearInterval(intervalID3);
                clearInterval(intervalID4);
                clearInterval(intervalID5);
            }
            $("#multiplier_first_digit").html(0);
            $("#multiplier_second_digit").html(0);
            $("#multiplier_third_digit").html(0);
            $("#multiplier_fourth_digit").html(0);
            $("#multiplier_fifth_digit").html(0);
            if (result[0] == "e6") {
                $("#double_your_btc_bet_hi_button").attr("disabled", true);
                $("#double_your_btc_bet_lo_button").attr("disabled", true);
            } else {
                $("#double_your_btc_bet_hi_button").attr("disabled", false);
                $("#double_your_btc_bet_lo_button").attr("disabled", false);
            }
        }
    }).fail(function() {
        $('#double_your_btc_result').show();
        $('#double_your_btc_error').show();
        $('#double_your_btc_digits').hide();
        $('#double_your_btc_error').html("Request timed out. Please try again.");
        if (enable_animation == 1) {
            clearInterval(intervalID1);
            clearInterval(intervalID2);
            clearInterval(intervalID3);
            clearInterval(intervalID4);
            clearInterval(intervalID5);
        }
        $("#multiplier_first_digit").html(0);
        $("#multiplier_second_digit").html(0);
        $("#multiplier_third_digit").html(0);
        $("#multiplier_fourth_digit").html(0);
        $("#multiplier_fifth_digit").html(0);
        $("#double_your_btc_bet_hi_button").attr("disabled", false);
        $("#double_your_btc_bet_lo_button").attr("disabled", false);
    });
}
function title_countdown(tot_time) {
    var countdown_end = (new Date() / 1000) + tot_time;
    setInterval(function() {
        if (tot_time < 1) {
            $('title').html('0m:0s - FreeBitco.in - Win free bitcoins every hour!');
            return;
        } else {
            tot_time = countdown_end - (new Date() / 1000) - 1;
            var mins = Math.floor(tot_time / 60);
            var secs = Math.floor(tot_time - (mins * 60));
            $('title').html(mins + 'm:' + secs + 's - ' + 'FreeBitco.in - Win free bitcoins every hour!');
        }
    }, 1000);
}
function ShowMoreRefs(count) {
    var refs_shown = parseInt($('#referrals_shown').val(), 0);
    $.get('/?op=show_more_refs&start=' + refs_shown + '&count=' + count, function(data) {
        $("#referral_list_table").append(data);
        if (count == 10) {
            $('#referrals_shown').val(refs_shown + 10);
        } else if (count == 20) {
            $('#referrals_shown').val(refs_shown + 20);
        } else if (count == 9999999) {
            $('#show_more_refs_options').hide();
        }
    });
}
function ShowAdvancedStats(days) {
    $.get('/?op=show_advanced_stats&days=' + days, function(data) {
        $('#detailed_ref_stats_table').show();
        $('#detailed_ref_stats_table').find("tr:gt(0)").remove();
        $("#detailed_ref_stats_table").append(data);
    });
}
function SwitchTabs() {
    $('#top_leader_iframe').attr('src', $('#top_leader_iframe').attr('src'));
    $('#left_sky_iframe').attr('src', $('#left_sky_iframe').attr('src'));
    $('#right_sky_iframe').attr('src', $('#right_sky_iframe').attr('src'));
}
function GenerateDepositAddress() {
    $.get('/?op=generate_bitcoin_deposit_address', function(data) {
        $('#deposit_address').html('<p>Send bitcoins to the address below to top up your advertising account balance.</p><p><div style="width:400px;"><input type="text" size=40 style="text-align:center;" value="' + data + '" onClick="this.select();"></div></p>');
    });
}
function DeleteAdCampaign(id) {
    var conf = confirm("Are you sure you wish to delete this ad campaign? Deleting an ad campaign also deletes it's stats. If you wish to stop running this ad but want to retain it's stats, please pause it instead. Click OK if you would like to proceed with deleting this ad campaign else click CANCEL.");
    if (conf == true) {
        $('#ad_campaign_' + id).hide();
        $('#ad_campaign_details_' + id).hide();
        $.get('/?op=delete_ad_campaign&id=' + id, function() {});
    }
}
function StartAdCampaign(id) {
    $('#start_pause_ad_campaign_icon_' + id).html('<a href="javascript:void(0);" onclick="PauseAdCampaign(' + id + ');"><img src="//static1.freebitco.in/images/pause3.png" border=0 alt="PAUSE"></a>');
    $('#ad_campaign_status_' + id).html('<span style="color:#006600;">RUNNING</span>');
    $.get('/?op=start_ad_campaign&id=' + id, function(data) {
        if (data == "e2") {
            $('#ad_campaign_status_' + id).html('<span style="color:red;">REFILL ADVERTISING ACCOUNT</span>');
            $('#start_pause_ad_campaign_icon_' + id).html('');
        }
    });
}
function PauseAdCampaign(id) {
    $('#start_pause_ad_campaign_icon_' + id).html('<a href="javascript:void(0);" onclick="StartAdCampaign(' + id + ');"><img src="//static1.freebitco.in/images/start4.png" border=0 alt="START"></a>');
    $('#ad_campaign_status_' + id).html('<span style="color:red;">PAUSED</span>');
    $.get('/?op=pause_ad_campaign&id=' + id, function() {});
}
function ShowAdDetails(id) {
    $('#ad_details_table').hide();
    $('#edit_ad_error').hide();
    $('#edit_ad_success').hide();
    $.get('/?op=show_ad_details&id=' + id, function(data) {
        var result = data.split(":");
        $('#ad_details_table').show();
        $('#ad_details_popup_campaign_name').val(result[0]);
        $('#ad_details_popup_banner_image').attr("src", "//fbtc-uab.freebitco.in/" + result[1]);
        $('#ad_details_popup_daily_budget').val(result[2]);
        $('#ad_details_popup_total_budget').val(result[3]);
        $('#ad_details_popup_destination_url').val(result[4]);
        $('#ad_details_popup_max_cpm').val(result[8]);
        $('#ad_details_popup_ad_id').val(id);
        $('#ad_details_popup_freq_cap').val(result[9]);
        var target_countries = result[10].split(",");
        if (result[5] == '1') {
            $('#ad_details_popup_adv_bit').prop('checked', true);
        } else {
            $('#ad_details_popup_adv_bit').prop('checked', false);
        }
        if (result[6] == '1') {
            $('#ad_details_popup_adv_doge').prop('checked', true);
        } else {
            $('#ad_details_popup_adv_doge').prop('checked', false);
        }
        var ad_position = result[7].split("_");
        $('#ad_details_popup_ad_position').html(ad_position[0].charAt(0).toUpperCase() + ad_position[0].slice(1) + ' - ' + ad_position[1] + 'x' + ad_position[2]);
        $("#ad_details_target_country_code").val(target_countries);
    });
}
function ShowAdStats(id) {
    $('#daily_ad_stats_table').hide();
    $.get('/?op=show_daily_ad_stats&id=' + id, function(data) {
        var rows = data.split(";");
        $('#daily_ad_stats_campaign_name').html(rows[0]);
        rows.shift();
        $('#daily_ad_stats_table').show();
        $('#daily_ad_stats_table').find("tr:gt(0)").remove();
        var last_but = rows.length - 1;
        for (var i = 0; i < last_but; i++) {
            var elements = rows[i].split(":");
            $("#daily_ad_stats_table").append("<tr><td>" + elements[0] + "</td><td>" + commaSeparateNumber(elements[1]) + "</td><td>" + commaSeparateNumber(elements[2]) + "</td><td>" + elements[3] + "</td><td>" + elements[4] + "</td></tr>");
        }
        var totals = rows[last_but].split(":");
        $("#daily_ad_stats_table").append("<tr><td class=bold>TOTAL</td><td>" + commaSeparateNumber(totals[0]) + "</td><td>" + commaSeparateNumber(totals[1]) + "</td><td>" + totals[2] + "</td><td>" + totals[3] + "</td></tr>");
    });
}
function RefreshAdBalance() {
    $.get('/?op=refresh_ad_balance', function(data) {
        $('#ad_balance').html(data);
    });
}
function UpdateAdStats() {
    $.get('/?op=update_ad_stats', function(data) {
        var rows = data.split(";");
        for (var i = 0; i < rows.length; i++) {
            var elements = rows[i].split(":");
            if (elements[1] < 2) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:#FF6600;'>PENDING APPROVAL</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("");
            } else if (elements[1] == 2) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:#006600;'>APPROVED</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("<a href='javascript:void(0);' onclick='StartAdCampaign(" + elements[0] + ");'><img src='//static1.freebitco.in/images/start4.png' border=0 alt='START'></a>");
            } else if (elements[1] == 3) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:#006600;'>RUNNING</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("<a href='javascript:void(0);' onclick='PauseAdCampaign(" + elements[0] + ");'><img src='//static1.freebitco.in/images/pause3.png' border=0 alt='PAUSE'></a>");
            } else if (elements[1] == 4) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:red;'>PAUSED</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("<a href='javascript:void(0);' onclick='StartAdCampaign(" + elements[0] + ");'><img src='//static1.freebitco.in/images/start4.png' border=0 alt='START'></a>");
            } else if (elements[1] == 5) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:red;'>REJECTED&nbsp;<a href='javascript:void(0);' onclick='GetAdRejectedReason(" + elements[0] + ", " + elements[5] + ");'>?</a></span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("");
            } else if (elements[1] == 6) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:red;'>EXCEEDED DAILY BUDGET</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("<a href='javascript:void(0);' onclick='PauseAdCampaign(" + elements[0] + ");'><img src='//static1.freebitco.in/images/pause3.png' border=0 alt='PAUSE'></a>");
            } else if (elements[1] == 7) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:red;'>EXCEEDED TOTAL BUDGET</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("<a href='javascript:void(0);' onclick='PauseAdCampaign(" + elements[0] + ");'><img src='//static1.freebitco.in/images/pause3.png' border=0 alt='PAUSE'></a>");
            } else if (elements[1] == 8) {
                $('#ad_campaign_status_' + elements[0]).html("<span style='color:red;'>REFILL ADVERTISING ACCOUNT</span>");
                $('#start_pause_ad_campaign_icon_' + elements[0]).html("");
            }
            $('#campaign_views_' + elements[0]).html(commaSeparateNumber(elements[2]));
            $('#campaign_clicks_' + elements[0]).html(commaSeparateNumber(elements[3]));
            $('#campaign_total_cost_' + elements[0]).html(elements[4]);
        }
    });
}
function commaSeparateNumber(val) {
    while (/(\d+)(\d{3})/.test(val.toString())) {
        val = val.toString().replace(/(\d+)(\d{3})/, '$1' + ',' + '$2');
    }
    return val;
}
function AutoBet(mode, bet_count, max_bet, base_bet, autobet_win_return_to_base, autobet_lose_return_to_base, autobet_win_increase_bet_percent, autobet_lose_increase_bet_percent, change_client_seed, reset_after_max_bet, rolls_played, biggest_bet, biggest_win, session_pl, autobet_win_change_odds, autobet_lose_change_odds, stop_after_profit, stop_after_loss, logging, enable_sounds) {
    if (stop_autobet == true) {
        StopAutoBet();
        return;
    } else {
        $('#double_your_btc_digits').show();
        $('#autobet_results_box').show();
        $("#double_your_btc_bet_hi_button").attr("disabled", true);
        $("#double_your_btc_bet_lo_button").attr("disabled", true);
        var bet = parseFloat($("#double_your_btc_stake").val()).toFixed(8);
        if (parseFloat(bet) > parseFloat(biggest_bet)) {
            biggest_bet = bet;
        }
        autobet_win_increase_bet_percent = parseFloat(autobet_win_increase_bet_percent).toFixed(2);
        autobet_lose_increase_bet_percent = parseFloat(autobet_lose_increase_bet_percent).toFixed(2);
        var jackpot = 0;
        var jackpot_arr = $('.play_jackpot:checkbox:checked').map(function() {
            return this.value;
        }).get();
        if (jackpot_arr.length > 0) {
            jackpot = jackpot_arr.toString();
        }
        var client_seed = $('#next_client_seed').val();
        var new_mode = mode;
        if (mode == "alternate") {
            if (bet_count % 2 == 0) {
                new_mode = "hi";
            } else {
                new_mode = "lo";
            }
        }
        $.get('/cgi-bin/bet.pl?m=' + new_mode + '&client_seed=' + client_seed + '&jackpot=' + jackpot + '&stake=' + bet + '&multiplier=' + $("#double_your_btc_payout_multiplier").val() + '&rand=' + Math.random(), function(data) {
            var result = data.split(":");
            $('#double_your_btc_error').html("");
            $('#double_your_btc_error').hide();
            $('#double_your_btc_stake').removeClass('input-error');
            $('#double_your_btc_bet_win').html("");
            $('#double_your_btc_bet_lose').html("");
            $('#double_your_btc_bet_win').hide();
            $('#double_your_btc_bet_lose').hide();
            $('#jackpot_message').removeClass('green');
            $('#jackpot_message').removeClass('red');
            $('#jackpot_message').html('');
            $('#jackpot_message').hide();
            $('#double_your_btc_result').show();
            if (result[0] == "s1") {
                bet_count--;
                rolls_played++;
                $('#rolls_played_count').html(rolls_played);
                $('#rolls_remaining_count').html(bet_count);
                $('#autobet_highest_bet').html(biggest_bet + " BTC");
                var number = result[2];
                var single_digit = number.split("");
                if (number.toString().length < 5) {
                    var remaining = 5 - number.toString().length;
                    for (var i = 0; i < remaining; i++) {
                        single_digit.unshift('0');
                    }
                }
                $("#multiplier_first_digit").html(single_digit[0]);
                $("#multiplier_second_digit").html(single_digit[1]);
                $("#multiplier_third_digit").html(single_digit[2]);
                $("#multiplier_fourth_digit").html(single_digit[3]);
                $("#multiplier_fifth_digit").html(single_digit[4]);
                $('#balance').html(result[3]);
                max_deposit_bonus = parseFloat(result[18]).toFixed(8);
                balanceChanged();
                $('#balance_usd').html(result[5]);
                $('#next_server_seed_hash').val(result[6]);
                $('#next_nonce').html(result[8]);
                $('.previous_server_seed').html(result[9]);
                $('.previous_server_seed').val(result[9]);
                $('#previous_server_seed_hash').val(result[10]);
                $('.previous_client_seed').html(result[11]);
                $('.previous_client_seed').val(result[11]);
                $('.previous_nonce').html(result[12]);
                $('#previous_roll').html(result[2]);
                $('#no_previous_rolls_msg').hide();
                $('#previous_rolls_table').show();
                $('#previous_roll_strings').show();
                $('#bonus_account_balance').html(result[16] + " BTC");
                $('#bonus_account_wager').html(result[17] + " BTC");
                if ((parseFloat(result[16]) <= 0 || parseFloat(result[17]) <= 0) && bonus_table_closed == 0) {
                    setTimeout(function() {
                        $('#bonus_account_table').hide();
                        $('#user_claimed_deposit_bonus').hide();
                        bonus_table_closed = 1;
                    }, 5000);
                }
                if (max_deposit_bonus >= parseFloat(min_bonus_amount) && bonus_table_closed == 1) {
                    $('#bonus_eligible_msg').show();
                }
                if (parseFloat(result[19]) > 0 && parseFloat(result[19]) < 100) {
                    $('.multiply_max_bet').html(result[19] + " BTC");
                    $('.multiply_max_bet').val(result[19]);
                    max_win_amount = parseFloat(result[19]);
                }
                $("#verify_rolls_link").attr("href", "https://s3.amazonaws.com/roll-verifier/verify.html?server_seed=" + result[9] + "&client_seed=" + result[11] + "&server_seed_hash=" + result[10] + "&nonce=" + result[12]);
                insertIntoBetHistory(result[1], result[4], result[2], result[9], result[11], result[10], result[12], "DICE", new_mode, jackpot, bet, $("#double_your_btc_payout_multiplier").val(), result[20], result[21], result[22], result[23]);
                var capsmode = new_mode.toUpperCase();
                var bet_profit = "";
                if (result[1] == "w") {
                    $('#double_your_btc_bet_win').show();
                    $('#double_your_btc_bet_win').html("You BET " + capsmode + " so you win " + result[4] + " BTC!");
                    bet_profit = "<font color=green>+" + result[4] + "</font>";
                    session_pl = parseFloat(((session_pl * 100000000) + (result[4] * 100000000)) / 100000000).toFixed(8);
                    if (autobet_win_return_to_base == 1) {
                        $("#double_your_btc_stake").val(parseFloat(base_bet).toFixed(8));
                    } else if (parseFloat(autobet_win_increase_bet_percent) != 0) {
                        var new_bet_size = parseFloat((bet * ((autobet_win_increase_bet_percent / 100) + 1))).toFixed(8);
                        $("#double_your_btc_stake").val(new_bet_size);
                    }
                    if (parseFloat(result[4]) > parseFloat(biggest_win)) {
                        biggest_win = parseFloat(result[4]).toFixed(8);
                    }
                    $('#autobet_highest_win').html(biggest_win + " BTC");
                    if (autobet_win_change_odds != 0) {
                        $("#double_your_btc_payout_multiplier").val(autobet_win_change_odds);
                        $("#double_your_btc_payout_multiplier").keyup();
                    }
                    if (enable_sounds === 1) {
                        $.ionSound.play("bell_ring");
                    }
                }
                if (result[1] == "l") {
                    $('#double_your_btc_bet_lose').show();
                    $('#double_your_btc_bet_lose').html("You BET " + capsmode + " so you lose " + result[4] + " BTC");
                    bet_profit = "<font color=red>-" + result[4] + "</font>";
                    session_pl = parseFloat(((session_pl * 100000000) - (result[4] * 100000000)) / 100000000).toFixed(8);
                    if (autobet_lose_return_to_base == 1) {
                        $("#double_your_btc_stake").val(parseFloat(base_bet).toFixed(8));
                    } else if (autobet_lose_increase_bet_percent != 0) {
                        var new_bet_size = parseFloat((bet * ((autobet_lose_increase_bet_percent / 100) + 1))).toFixed(8);
                        $("#double_your_btc_stake").val(new_bet_size);
                    }
                    if (autobet_lose_change_odds != 0) {
                        $("#double_your_btc_payout_multiplier").val(autobet_lose_change_odds);
                        $("#double_your_btc_payout_multiplier").keyup();
                    }
                    if (enable_sounds === 1) {
                        $.ionSound.play("tap");
                    }
                }
                if (jackpot != 0) {
                    $('#jackpot_message').show();
                    if (result[13] == "1") {
                        $('#jackpot_message').addClass('green');
                        $('#jackpot_message').html("Congratulations! You have won the jackpot of " + result[15] + " BTC");
                    } else {
                        $('#jackpot_message').addClass('red');
                        $('#jackpot_message').html("Sorry, you did not win the jackpot.");
                    }
                }
                $("#double_your_btc_bet_hi_button").attr("disabled", false);
                $("#double_your_btc_bet_lo_button").attr("disabled", false);
                $('#autobet_pl').removeClass();
                $('#autobet_pl').addClass('bold');
                if (parseFloat(session_pl) < 0) {
                    $('#autobet_pl').css({
                        'background-color': '#FF6666'
                    });
                } else {
                    $('#autobet_pl').css({
                        'background-color': '#33FF33'
                    });
                }
                $('#autobet_pl').html(session_pl + ' BTC');
                if (bet_count > 0) {
                    bet = parseFloat($("#double_your_btc_stake").val()).toFixed(8);
                    if (parseFloat(bet) > parseFloat(max_bet) || parseFloat(bet * ($("#double_your_btc_payout_multiplier").val() - 1)) > parseFloat(max_win_amount)) {
                        if (reset_after_max_bet == 1) {
                            $("#double_your_btc_stake").val(parseFloat(base_bet).toFixed(8));
                        } else {
                            stop_autobet = true;
                        }
                    }
                    if (change_client_seed == 1) {
                        charSet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
                        var randomString = '';
                        for (var i = 0; i < 16; i++) {
                            var randomPoz = Math.floor(Math.random() * charSet.length);
                            randomString += charSet.substring(randomPoz, randomPoz + 1);
                        }
                        $('#next_client_seed').val(randomString);
                    }
                    if ((parseFloat(stop_after_profit) > 0 && parseFloat(session_pl) >= parseFloat(stop_after_profit)) || (parseFloat(stop_after_loss) > 0 && parseFloat(session_pl) <= -1 * parseFloat(stop_after_loss))) {
                        stop_autobet = true;
                    }
                    AutoBet(mode, bet_count, max_bet, base_bet, autobet_win_return_to_base, autobet_lose_return_to_base, autobet_win_increase_bet_percent, autobet_lose_increase_bet_percent, change_client_seed, reset_after_max_bet, rolls_played, biggest_bet, biggest_win, session_pl, autobet_win_change_odds, autobet_lose_change_odds, stop_after_profit, stop_after_loss, logging, enable_sounds);
                } else {
                    StopAutoBet();
                }
            } else {
                $('#double_your_btc_error').show();
                $('#double_your_btc_digits').hide();
                if (parseFloat(result[1]) > 0 && parseFloat(result[1]) < 100) {
                    $('.multiply_max_bet').html(result[1] + " BTC");
                    $('.multiply_max_bet').val(result[1]);
                    max_win_amount = parseFloat(result[1]);
                }
                BetErrors(result[0]);
                StopAutoBet();
                if (result[0] == "e6") {
                    $("#double_your_btc_bet_hi_button").attr("disabled", true);
                    $("#double_your_btc_bet_lo_button").attr("disabled", true);
                } else {
                    $("#double_your_btc_bet_hi_button").attr("disabled", false);
                    $("#double_your_btc_bet_lo_button").attr("disabled", false);
                }
            }
        }).fail(function() {
            AutoBet(mode, bet_count, max_bet, base_bet, autobet_win_return_to_base, autobet_lose_return_to_base, autobet_win_increase_bet_percent, autobet_lose_increase_bet_percent, change_client_seed, reset_after_max_bet, rolls_played, biggest_bet, biggest_win, session_pl, autobet_win_change_odds, autobet_lose_change_odds, stop_after_profit, stop_after_loss, logging);
        });
    }
}
function RefreshPageAfterFreePlayTimerEnds() {
    if (autobet_dnr == false) {
        if (free_play_sound == true) {
            $.ionSound.play("jump_up");
        }
        window.location.replace("https://freebitco.in/?op=home");
    }
}
function StopAutoBet() {
    $("#double_your_btc_stake").val('0.00000001');
    $("#double_your_btc_payout_multiplier").val(2);
    $("#double_your_btc_payout_multiplier").keyup();
    $(".play_jackpot").prop("checked", false);
    $("#auto_betting_button").show();
    $("#stop_auto_betting").hide();
    stop_autobet = false;
    autobet_running = false;
    autobet_dnr = false;
    $("#start_autobet").removeClass('close-reveal-modal');
}
function GenerateMainDepositAddress() {
    $.get('/?op=generate_main_bitcoin_deposit_address', function(data) {
        var result = data.split(":");
        DisplaySEMessage(result[0], result[1]);
        if (result[0] == "s") {
            $("#main_deposit_address_box").show();
            $("#main_deposit_address_qr_code").show();
            $("#main_deposit_address_qr_code").html('<img src="//chart.googleapis.com/chart?cht=qr&chs=200x200&chl=' + result[2] + '&chld=H|0">');
            $("#main_deposit_address").val(result[2]);
            $("#generate_new_address_msg").hide();
        }
    });
}
function myDecisionFunction() {
    if (submissionEnabled) {
        submissionEnabled = false;
        return true;
    } else {
        return false;
    }
}
function GetAdRejectedReason(ad_id, reject_code) {
    var common = "Ad Rejection Reason: ";
    if (reject_code == 1) {
        alert(common + "Banner is too distracting.");
    } else if (reject_code == 2) {
        alert(common + "Banner or website contains 18+ content.");
    } else if (reject_code == 3) {
        alert(common + "Destination URL is invalid or does not load.");
    } else if (reject_code == 4) {
        $.get('/?op=banner_reject_reason&id=' + ad_id, function(data) {
            alert(common + data);
        });
    }
}
function UpdateUserStats() {
    if (socket_userid > 0) {
        $.get('/stats_new_private/?u=' + socket_userid + '&p=' + socket_password + '&f=user_stats', function(data) {
            if (data.status == "success") {
                var user_lottery_tickets = parseInt(data.lottery_tickets);
                var user_reward_points = parseInt(data.user_extras.reward_points);
                var user_balance = parseInt(data.user.balance);
                var golden_lottery_tickets = parseInt(data.lambo_lottery_tickets);
                var total_golden_lottery_tickets = parseInt(data.total_lambo_lottery_tickets);
                if (user_balance > 0) {
                    if ((Math.floor(Date.now() / 1000)) - balance_last_changed > 30) {
                        $('#balance').html(parseFloat(user_balance / 100000000).toFixed(8));
                        $("#earn_btc_acc_balance").val($('#balance').html());
                        $("#earn_btc_acc_balance").keyup();
                        balanceChanged();
                    }
                }
                if (user_lottery_tickets >= 0) {
                    $('#user_lottery_tickets').html(ReplaceNumberWithCommas(user_lottery_tickets));
                }
                if (user_reward_points >= 0) {
                    $('.user_reward_points').html(ReplaceNumberWithCommas(user_reward_points));
                }
                if (golden_lottery_tickets >= 0 && total_golden_lottery_tickets >= 0) {
                    var lambo_win_chance = parseFloat((golden_lottery_tickets / total_golden_lottery_tickets) * 100).toFixed(8);
                    $("#golden_lottery_win_chance").html(lambo_win_chance);
                }
                if (golden_lottery_tickets >= 0) {
                    $('#user_golden_lottery_tickets').html(ReplaceNumberWithCommas(golden_lottery_tickets));
                }
                if (total_golden_lottery_tickets >= 0) {
                    CountupTimer("#total_golden_lottery_tickets", total_golden_lottery_tickets, 1.1, 0);
                }
                if (data.unconf_tx.length > 0 && hide_pending_deposits == 0) {
                    $('#unconfirmed_deposits_table').show();
                    $('#unconfirmed_deposits_table_rows').html('');
                    var mobile_class = "";
                    if (mobile_device == 1) {
                        mobile_class = "lottery_table_mobile_style";
                    }
                    for (var i = 0; i < data.unconf_tx.length; i++) {
                        var tx_hash = data.unconf_tx[i].tx_hash.substring(0, 12) + "..." + data.unconf_tx[i].tx_hash.substring(data.unconf_tx[i].tx_hash.length - 12);
                        if (mobile_device == 1) {
                            tx_hash = data.unconf_tx[i].tx_hash.substring(0, 10) + "..." + data.unconf_tx[i].tx_hash.substring(data.unconf_tx[i].tx_hash.length - 10);
                        }
                        var amount = parseFloat(data.unconf_tx[i].amount / 100000000).toFixed(8);
                        $('#unconfirmed_deposits_table_rows').append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-8 small-8 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"><a href="https://www.blocktrail.com/BTC/tx/' + data.unconf_tx[i].tx_hash + '" target=_blank>' + tx_hash + '</a></div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '">' + amount + '</div></div></div>');
                    }
                } else {
                    $('#unconfirmed_deposits_table').hide();
                }
                var show_payout_table = 0;
                var payout_blocked = 0;
                if (data.instant_payment_requests.length > 0 && hide_pending_payments == 0) {
                    show_payout_table = 1;
                    $('#instant_pending_payout_table').show();
                    $('#instant_pending_payout_table').html('');
                    var mobile_class = "";
                    if (mobile_device == 1) {
                        mobile_class = "lottery_table_mobile_style";
                    }
                    $('#instant_pending_payout_table').append('<div class="large-12 small-12 columns center lottery_winner_table_box"><div class="center bold" style="margin:auto;">INSTANT</div></div>');
                    for (var i = 0; i < data.instant_payment_requests.length; i++) {
                        var amount = parseFloat(data.instant_payment_requests[i].amount / 100000000).toFixed(8);
                        var btc_address = data.instant_payment_requests[i].btc_address;
                        if (mobile_device == 1) {
                            btc_address = data.instant_payment_requests[i].btc_address.substring(0, 10) + "..." + data.instant_payment_requests[i].btc_address.substring(data.instant_payment_requests[i].btc_address.length - 10);
                        }
                        $('#instant_pending_payout_table').append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-8 small-8 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"><a href="https://btc.com/' + data.instant_payment_requests[i].btc_address + '" target=_blank>' + btc_address + '</a></div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '">' + amount + '</div></div>');
                        if (data.instant_payment_requests[i].block == 1) {
                            payout_blocked = 1;
                        }
                    }
                } else {
                    $('#instant_pending_payout_table').hide();
                }
                if (data.manual_payment_requests.length > 0 && hide_pending_payments == 0) {
                    show_payout_table = 1;
                    $('#pending_payout_table').show();
                    $('#pending_payout_table').html('');
                    var mobile_class = "";
                    if (mobile_device == 1) {
                        mobile_class = "lottery_table_mobile_style";
                    }
                    $('#pending_payout_table').append('<div class="large-12 small-12 columns center lottery_winner_table_box"><div class="center bold" style="margin:auto;">SLOW</div></div>');
                    for (var i = 0; i < data.manual_payment_requests.length; i++) {
                        var amount = parseFloat(data.manual_payment_requests[i].amount / 100000000).toFixed(8);
                        var btc_address = data.manual_payment_requests[i].btc_address;
                        if (mobile_device == 1) {
                            btc_address = data.manual_payment_requests[i].btc_address.substring(0, 10) + "..." + data.manual_payment_requests[i].btc_address.substring(data.manual_payment_requests[i].btc_address.length - 10);
                        }
                        $('#pending_payout_table').append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-8 small-8 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"><a href="https://btc.com/' + data.manual_payment_requests[i].btc_address + '" target=_blank>' + btc_address + '</a></div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '">' + amount + '</div></div>');
                        if (data.manual_payment_requests[i].block == 1) {
                            payout_blocked = 1;
                        }
                    }
                } else {
                    $('#pending_payout_table').hide();
                }
                if (show_payout_table == 1) {
                    $('#pending_payouts_table_new').show();
                    if (payout_blocked == 1) {
                        $('#payout_manual_review_msg').show();
                    }
                } else {
                    $('#pending_payouts_table_new').hide();
                }
                if (parseInt(data.unblock_gbr.lottery_to_unblock) > 0 && parseFloat(data.unblock_gbr.deposit_to_unblock) > 0 && parseFloat(data.unblock_gbr.jackpot_to_unblock) > 0 && parseFloat(data.unblock_gbr.wager_to_unblock) > 0) {
                    if (new_user_first_load == 0) {
                        $('#req_for_bonuses_link').show();
                    }
                    $('#unblock_modal_rp_bonuses_container').show();
                    $('#unblock_modal_rp_bonuses').html('<p>To play FREE BTC using a VPN/proxy, to be able to redeem all reward point bonuses and to get an alternative to recaptcha:</p><div class="bold center account_unblock_options_box" id="option_container_play_multiply"><p class="bold">Purchase <span class="account_unblock_span option_play_multiply_span">' + ReplaceNumberWithCommas(data.unblock_gbr.lottery_to_unblock) + '</span> lottery tickets</p></div><h5 style="text-align: center;">OR</h5><div class="bold center account_unblock_options_box" id="option_container_deposit"><p class="bold">Wager <span class="account_unblock_span option_deposit_span">' + data.unblock_gbr.jackpot_to_unblock + ' BTC</span> in MULTIPLY BTC jackpots</p></div><h5 style="text-align: center;">OR</h5><div class="bold center account_unblock_options_box" id="option_container_buy_lottery"><p class="bold">Wager <span class="account_unblock_span option_buy_lottery_span">' + data.unblock_gbr.wager_to_unblock + ' BTC</span> in MULTIPLY BTC</p></div><h5 style="text-align: center;">OR</h5><div class="bold center account_unblock_options_box" id="option_container_fp_bonus"><p class="bold">Deposit <span class="account_unblock_span option_fp_bonus_span">' + data.unblock_gbr.deposit_to_unblock + ' BTC</span> into your account to earn interest</p>');
                } else {
                    $('#unblock_modal_rp_bonuses_container').hide();
                }
                if (parseInt(data.no_captcha_gbr.lottery_to_unblock) > 0 && parseFloat(data.no_captcha_gbr.deposit_to_unblock) > 0 && parseFloat(data.no_captcha_gbr.jackpot_to_unblock) > 0 && parseFloat(data.no_captcha_gbr.wager_to_unblock) > 0) {
                    if (new_user_first_load == 0) {
                        $('#req_for_bonuses_link').show();
                    }
                    $('#unblock_modal_no_captcha_container').show();
                    $('#unblock_modal_no_captcha').html('<p>To play FREE BTC without having to solve a captcha:</p><div class="bold center account_unblock_options_box" id="option_container_play_multiply"><p class="bold">Purchase <span class="account_unblock_span option_play_multiply_span">' + ReplaceNumberWithCommas(data.no_captcha_gbr.lottery_to_unblock) + '</span> lottery tickets</p></div><h5 style="text-align: center;">OR</h5><div class="bold center account_unblock_options_box" id="option_container_deposit"><p class="bold">Wager <span class="account_unblock_span option_deposit_span">' + data.no_captcha_gbr.jackpot_to_unblock + ' BTC</span> in MULTIPLY BTC jackpots</p></div><h5 style="text-align: center;">OR</h5><div class="bold center account_unblock_options_box" id="option_container_buy_lottery"><p class="bold">Wager <span class="account_unblock_span option_buy_lottery_span">' + data.no_captcha_gbr.wager_to_unblock + ' BTC</span> in MULTIPLY BTC</p></div><h5 style="text-align: center;">OR</h5><div class="bold center account_unblock_options_box" id="option_container_fp_bonus"><p class="bold">Deposit <span class="account_unblock_span option_fp_bonus_span">' + data.no_captcha_gbr.deposit_to_unblock + ' BTC</span> into your account to earn interest</p>');
                } else {
                    $('#unblock_modal_no_captcha_container').hide();
                }
                if (typeof data.wager_contest.wager_personal !== "undefined") {
                    $('#personal_wager_for_contest').html(parseFloat(data.wager_contest.wager_personal / 100000000).toFixed(8));
                }
                if (typeof data.wager_contest.ref_contest_personal !== "undefined") {
                    $('#ref_wager_for_contest').html(parseFloat(data.wager_contest.ref_contest_personal / 100000000).toFixed(8));
                }
                if (typeof data.user_daily_jp !== "undefined" && data.user_daily_jp != null) {
                    user_daily_jp_rank = data.user_daily_jp.rank;
                    $('#daily_jp_user_rank').html('#' + user_daily_jp_rank);
                    $('#daily_jackpot_user_rank').html(ReplaceNumberWithCommas(user_daily_jp_rank));
                    user_daily_jp_wagered = parseFloat(data.user_daily_jp.wagered / 100000000).toFixed(8);
                    $('#daily_jackpot_user_wagered').html(user_daily_jp_wagered);
                }
            }
        });
        setTimeout(UpdateUserStats, 1.1 * 60 * 1000);
    }
}
function ReplaceNumberWithCommas(yourNumber) {
    var n = yourNumber.toString().split(".");
    n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return n.join(".");
}
function CalculateWinAmount() {
    $(".gt").html(parseInt(Math.round(10000 - (9500 / parseFloat($("#double_your_btc_payout_multiplier").val()).toFixed(2)))));
    $(".lt").html(parseInt(Math.round((9500 / parseFloat($("#double_your_btc_payout_multiplier").val()).toFixed(2)))));
    var win_amount = Math.floor(($("#double_your_btc_stake").val() * ((parseFloat(9500 / parseInt($(".lt").html())).toFixed(2)) - 1)) * 100000000 + 1e-6);
    $("#win_amount").html(parseFloat(win_amount / 100000000).toFixed(8));
}
function AutoBetErrors(code) {
    $("#autobet_error").show();
    $("#double_your_btc_result").hide();
    $("#double_your_btc_middle_section").css({
        'border-radius': '0 0 10px 10px'
    });
    if (code == "e1") {
        $("#autobet_error").html("Base bet has to be between 0.00000001 and " + max_win_amount + " BTC");
    }
    if (code == "e2") {
        $("#autobet_error").html("Bet odds has to be an integer between 1.01 and 4750");
    }
    if (code == "e3") {
        $("#autobet_error").html("Max bet has to be between 0.00000001 and " + max_win_amount + " BTC");
    }
    if (code == "e4") {
        $("#autobet_error").html("Bet count has to be atleast 1");
    }
    if (code == "e5") {
        $("#autobet_error").html("Bet odds after win has to be an integer between 1.01 and 4750");
    }
    if (code == "e6") {
        $("#autobet_error").html("Bet odds after lose has to be an integer between 1.01 and 4750");
    }
    if (code == "e7") {
        $("#autobet_error").html("Stop after profit value must be greater than 0");
    }
    if (code == "e8") {
        $("#autobet_error").html("Stop after loss value must be greater than 0");
    }
    if (code == "e13") {
        $('#autobet_error').html("Please deposit bitcoins first to make a bet using a multipllier over 100x.");
    }
}
function ScreeSizeCSSChanges() {
    if ($('body').innerWidth() < 1256) {
        $("#double_your_btc_middle_section").appendTo($("#double_your_btc_main_container_outer"));
        $("#double_your_btc_middle_section").css({
            'border-radius': '0 0 10px 10px'
        });
    }
    if ($('body').innerWidth() > 1100) {
        $('.change_size_css').addClass('large-7');
        $('.change_size_css').removeClass('large-10 large-12');
    }
    if ($('body').innerWidth() < 1256 && $('body').innerWidth() > 970) {
        $("#double_your_btc_main_container").addClass('double_your_btc_main_container_to_add');
        $("#double_your_btc_main_container").removeClass('double_your_btc_main_container_remove double_your_btc_main_container_to_add_small');
        $("#double_your_btc_left_section").addClass('double_your_btc_left_section_to_add');
        $("#double_your_btc_left_section").removeClass('double_your_btc_left_section_remove double_your_btc_left_section_to_add_small');
        $("#double_your_btc_middle_section").addClass('double_your_btc_middle_section_to_add');
        $("#double_your_btc_middle_section").removeClass('double_your_btc_middle_section_remove double_your_btc_middle_section_to_add_small');
        $("#double_your_btc_right_section").addClass('double_your_btc_right_section_to_add');
        $("#double_your_btc_right_section").removeClass('double_your_btc_right_section_remove double_your_btc_right_section_to_add_small');
        $("#double_your_btc_auto_bet_left_section").addClass('double_your_btc_left_section_to_add');
        $("#double_your_btc_auto_bet_left_section").removeClass('double_your_btc_left_section_remove double_your_btc_left_section_to_add_small');
        $("#double_your_btc_auto_bet_right_section").addClass('double_your_btc_auto_bet_right_section_to_add');
        $("#double_your_btc_auto_bet_right_section").removeClass('double_your_btc_auto_bet_right_section_remove double_your_btc_auto_bet_right_section_to_add_small');
        $("#bet_hi_button").addClass('bet_hi_button_to_add');
        $("#bet_hi_button").removeClass('bet_hi_button_remove bet_hi_button_to_add_small');
        $("#bet_lo_button").addClass('bet_lo_button_to_add');
        $("#bet_lo_button").removeClass('bet_lo_button_remove bet_lo_button_to_add_small');
    }
    if ($('body').innerWidth() < 1100 && $('body').innerWidth() > 970) {
        $('.change_size_css').addClass('large-10');
        $('.change_size_css').removeClass('large-7 large-12');
    }
    if ($('body').innerWidth() < 971) {
        $("#double_your_btc_main_container").addClass('double_your_btc_main_container_to_add_small');
        $("#double_your_btc_main_container").removeClass('double_your_btc_main_container_remove double_your_btc_main_container_to_add');
        $("#double_your_btc_left_section").addClass('double_your_btc_left_section_to_add_small');
        $("#double_your_btc_left_section").removeClass('double_your_btc_left_section_remove double_your_btc_left_section_to_add');
        $("#double_your_btc_middle_section").addClass('double_your_btc_middle_section_to_add_small');
        $("#double_your_btc_middle_section").removeClass('double_your_btc_middle_section_remove double_your_btc_middle_section_to_add');
        $("#double_your_btc_right_section").addClass('double_your_btc_right_section_to_add_small');
        $("#double_your_btc_right_section").removeClass('double_your_btc_right_section_remove double_your_btc_right_section_to_add');
        $("#double_your_btc_auto_bet_left_section").addClass('double_your_btc_left_section_to_add_small');
        $("#double_your_btc_auto_bet_left_section").removeClass('double_your_btc_left_section_remove double_your_btc_left_section_to_add');
        $("#double_your_btc_auto_bet_right_section").addClass('double_your_btc_auto_bet_right_section_to_add_small');
        $("#double_your_btc_auto_bet_right_section").removeClass('double_your_btc_auto_bet_right_section_remove double_your_btc_auto_bet_right_section_to_add');
        $("#bet_hi_button").addClass('bet_hi_button_to_add_small');
        $("#bet_hi_button").removeClass('bet_hi_button_remove bet_hi_button_to_add');
        $("#bet_lo_button").addClass('bet_lo_button_to_add_small');
        $("#bet_lo_button").removeClass('bet_lo_button_remove bet_lo_button_to_add');
        $('.change_size_css').addClass('large-12');
        $('.change_size_css').removeClass('large-7 large-10');
    }
}
function ShowNews(id) {
    $("#news_content_" + id).show();
}
function GetNewsContent(location, id, pos) {
    $.get('/?op=get_news_content&id=' + id, function(data) {
        $("#news_content_" + id).remove();
        $(pos).parent().after('<div class="large-11 small-12 large-centered columns ' + location + 'news_content" style="text-align:left;" id="news_content_' + id + '">' + data + '</div>');
    });
}
function GetInterestHistory() {
    if (userid > 0) {
        $.get('/stats_new_private/?u=' + socket_userid + '&p=' + socket_password + '&f=interest_history', function(data) {
            if (data.length > 0) {
                var mobile_class = "";
                if (mobile_device == 1) {
                    mobile_class = " lottery_table_mobile_style ";
                }
                $("#interest_history_table").html('<div class="large-12 small-12 columns center lottery_winner_table_box table_header_background br_5_5"><div class="center" style="margin:auto;">RECENT INTEREST PAYMENTS</div></div><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> <span class="bold">DATE</span> </div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell ' + mobile_class + '"> <span class="bold">BALANCE</span> </div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> <span class="bold">INTEREST</span> </div></div>');
                for (var i = 0; i < data.length; i++) {
                    $("#interest_history_table").append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + data[i].date + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell' + mobile_class + '">' + data[i].balance + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + data[i].interest + '</div> </div>');
                }
                $("#interest_history_table_row").show();
            }
        });
    }
}
function GetBetHistory(page) {
    if (page >= 0) {
        bet_history_page = page;
        $("#newer_bet_history").attr("disabled", true);
        $("#older_bet_history").attr("disabled", true);
        $.get('/stats_new_private/?u=' + socket_userid + '&p=' + socket_password + '&f=bet_history&page=' + page, function(data) {
            $('#bet_history_table_rows').html('');
            var result1 = data.split(";");
            var loop_end = 0;
            if (result1[0] == "success") {
                loop_end = 1;
            }
            for (var i = result1.length - 2; i >= loop_end; i--) {
                var result2 = result1[i].split(":");
                result2[0] = result2[0].replace(/\./g, ":");
                var time = formatDate(result2[0] + ' MST');
                if (result2[10] == "m") {
                    result2[10] = "DICE"
                } else if (result2[10] == "f") {
                    result2[10] = "FREE"
                } else if (result2[10] == "r") {
                    result2[10] = "ROULETTE"
                }
                var outcome = "l";
                if (result2[3] >= 0) {
                    outcome = "w";
                }
                result2[3] = result2[3].replace('-', '');
                insertIntoBetHistory(outcome, result2[3], result2[2], result2[7], result2[8], result2[11], result2[9], result2[10], result2[4], result2[5], result2[6], result2[1], result2[12], result2[13], result2[14], result2[15], time);
            }
            $("#newer_bet_history").attr("disabled", false);
            $("#older_bet_history").attr("disabled", false);
        });
    }
}
function formatDate(date) {
    if (date) {
        date = date.replace(/-/g, "/");
    }
    var d = new Date(date || Date.now());
    var month = '' + (d.getMonth() + 1)
      , day = '' + d.getDate()
      , year = d.getFullYear()
      , hour = '' + d.getHours()
      , minute = '' + d.getMinutes()
      , sec = '' + d.getSeconds();
    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;
    if (hour.length < 2)
        hour = '0' + hour;
    if (minute.length < 2)
        minute = '0' + minute;
    if (sec.length < 2)
        sec = '0' + sec;
    var formattedDate = day + "/" + month + "/" + year + " " + hour + ":" + minute + ":" + sec;
    return formattedDate;
}
function SwitchPageTabs(tab_name) {
    $(".page_tabs").hide();
    $("#" + tab_name + "_tab").show();
    $("#box_ad_bottom_mobile").hide();
    $("#box_ad_bottom_desktop").hide();
    $("#main_content_ad_left").hide();
    $("#main_content").css("padding", 'auto');
    $("#main_content").css("margin", 'auto');
    $("html, body").animate({
        scrollTop: 0
    }, "fast");
    $('.tabs li').removeClass('active');
    $('.' + tab_name + '_link').parent().addClass('active');
    if (tab_name == "free_play") {
        $("#box_ad_bottom_mobile").show();
        $("#box_ad_bottom_desktop").show();
        if (show_sky == 1) {
            $("#main_content").addClass('push-3');
            $("#main_content").removeClass('large-centered new_border_shadow');
            $("#main_content_ad_left").show();
            $("#main_content").css("padding", 0);
            $("#main_content").css("margin", 0);
        }
    }
    if (tab_name == "double_your_btc") {
        $("#myModal22").foundation('reveal', 'close');
        GetBetHistory(0);
        $(".deposit_promo_message_content").hide();
    } else {
        $(".deposit_promo_message_content").show();
    }
    if (tab_name == "earn_btc") {
        $("#myModal15").foundation('reveal', 'close');
        GetInterestHistory();
    }
    if (tab_name == "betting") {
        changeContainerDiv_parimutuel();
        LoadParimutuelBetsMain();
    } else {
        changeContainerDiv_others_parimutuel();
    }
    if (tab_name == "golden_ticket") {
        $("#myModal16").foundation('reveal', 'close');
    }
    if (tab_name == "stats" || tab_name == "rewards") {
        InitialStatsLoad();
    }
    if (tab_name == "wager_promotion") {
        PreviousContestWinners(wagering_contest_winners_round_display);
    }
}
function insertBitcoinMore(div_name, position) {
    document.getElementById(div_name).insertAdjacentHTML(position, '<div class ="row"><div class="large-12 small-12 large-centered small-centered columns" style="text-align:center;"><iframe id="ytplayer" type="text/html" src="//www.youtube.com/embed/Gc2en3nHxA4?fs=1&amp;hl=en_US&amp;rel=0&amp;hd=1" frameborder="0" allowfullscreen></iframe></div></div><p class="faq_question bold">What is Bitcoin?</p><div class="faq_answer"><p>Bitcoin is an innovative payment network and a new kind of money.</p><p>Bitcoin uses peer-to-peer technology to operate with no central authority or banks; managing transactions and the issuing of bitcoins is carried out collectively by the network. <b>Bitcoin is open-source; its design is public, nobody owns or controls Bitcoin and everyone can take part.</b> Through many of its unique properties, Bitcoin allows exciting uses that could not be covered by any previous payment system.</p></div><p class="faq_question bold">How does Bitcoin work?</p><div class="faq_answer"><p>From a user perspective, Bitcoin is nothing more than a mobile app or computer program that provides a personal Bitcoin wallet and allows a user to send and receive bitcoins with them. This is how Bitcoin works for most users.</p><p>Behind the scenes, the Bitcoin network is sharing a public ledger called the "block chain". This ledger contains every transaction ever processed, allowing a user&rsquo;s computer to verify the validity of each transaction. The authenticity of each transaction is protected by digital signatures corresponding to the sending addresses, allowing all users to have full control over sending bitcoins from their own Bitcoin addresses. In addition, anyone can process transactions using the computing power of specialized hardware and earn a reward in bitcoins for this service. This is often called "mining". To learn more about Bitcoin, you can consult the dedicated page and the original paper.</p></div><p class="faq_question bold">Who created Bitcoin?</p><div class="faq_answer"><p>Bitcoin is the first implementation of a concept called "cryptocurrency", which was first described in 1998 by Wei Dai on the cypherpunks mailing list, suggesting the idea of a new form of money that uses cryptography to control its creation and transactions, rather than a central authority. The first Bitcoin specification and proof of concept was published in 2009 in a cryptography mailing list by Satoshi Nakamoto. Satoshi left the project in late 2010 without revealing much about himself. The community has since grown exponentially with many developers working on Bitcoin.</p><p>Satoshi&rsquo;s anonymity often raised unjustified concerns, many of which are linked to misunderstanding of the open-source nature of Bitcoin. The Bitcoin protocol and software are published openly and any developer around the world can review the code or make their own modified version of the Bitcoin software. Just like current developers, Satoshi&rsquo;s influence was limited to the changes he made being adopted by others and therefore he did not control Bitcoin. As such, the identity of Bitcoin&rsquo;s inventor is probably as relevant today as the identity of the person who invented paper.</p></div><p class="faq_question bold">Who controls the Bitcoin network?</p><div class="faq_answer"><p>Nobody owns the Bitcoin network much like no one owns the technology behind email. Bitcoin is controlled by all Bitcoin users around the world. While developers are improving the software, they can&rsquo;t force a change in the Bitcoin protocol because all users are free to choose what software and version they use. In order to stay compatible with each other, all users need to use software complying with the same rules. Bitcoin can only work correctly with a complete consensus among all users. Therefore, all users and developers have a strong incentive to protect this consensus.</p></div><p class="faq_question bold">Is Bitcoin really used by people?</p><div class="faq_answer"><p>Yes. There is a growing number of businesses and individuals using Bitcoin. This includes brick and mortar businesses like restaurants, apartments, law firms, and popular online services such as Namecheap, WordPress, and Reddit. While Bitcoin remains a relatively new phenomenon, it is growing fast. At the end of August 2013, the value of all bitcoins in circulation exceeded US$ 1.5 billion with millions of dollars worth of bitcoins exchanged daily.</p></div><p class="faq_question bold">How does one acquire bitcoins?</p><div class="faq_answer"><p><ul style="text-align:left;"><li>As payment for goods or services.</li><li>Purchase bitcoins at a Bitcoin exchange.</li><li>Exchange bitcoins with someone near you.</li><li>Earn bitcoins through competitive mining.</li></ul></p><p>While it may be possible to find individuals who wish to sell bitcoins in exchange for a credit card or PayPal payment, most exchanges do not allow funding via these payment methods. This is due to cases where someone buys bitcoins with PayPal, and then reverses their half of the transaction. This is commonly referred to as a chargeback.</p></div><p class="faq_question bold">How difficult is it to make a Bitcoin payment?</p><div class="faq_answer"><p>Bitcoin payments are easier to make than debit or credit card purchases, and can be received without a merchant account. Payments are made from a wallet application, either on your computer or smartphone, by entering the recipient&rsquo;s address, the payment amount, and pressing send. To make it easier to enter a recipient&rsquo;s address, many <a href="https://freebitco.in/site/bitcoin-wallet/" target="blank">Bitcoin wallets</a> can obtain the address by scanning a QR code or touching two phones together with NFC technology.</p></div><p class="faq_question bold">What are the advantages of Bitcoin?</p><div class="faq_answer"><p><ul style="text-align:left;"><li>Payment freedom - It is possible to send and receive any amount of money instantly anywhere in the world at any time. No bank holidays. No borders. No imposed limits. Bitcoin allows its users to be in full control of their money.</li><li>Very low fees - Bitcoin payments are currently processed with either no fees or extremely small fees. Users may include fees with transactions to receive priority processing, which results in faster confirmation of transactions by the network. Additionally, merchant processors exist to assist merchants in processing transactions, converting bitcoins to fiat currency and depositing funds directly into merchants&rsquo; bank accounts daily. As these services are based on Bitcoin, they can be offered for much lower fees than with PayPal or credit card networks.</li><li>Fewer risks for merchants - Bitcoin transactions are secure, irreversible, and do not contain customers sensitive or personal information. This protects merchants from losses caused by fraud or fraudulent chargebacks, and there is no need for PCI compliance. Merchants can easily expand to new markets where either credit cards are not available or fraud rates are unacceptably high. The net results are lower fees, larger markets, and fewer administrative costs.</li><li>Security and control - Bitcoin users are in full control of their transactions; it is impossible for merchants to force unwanted or unnoticed charges as can happen with other payment methods. Bitcoin payments can be made without personal information tied to the transaction. This offers strong protection against identity theft. Bitcoin users can also protect their money with backup and encryption.</li><li>Transparent and neutral - All information concerning the Bitcoin money supply itself is readily available on the block chain for anybody to verify and use in real-time. No individual or organization can control or manipulate the Bitcoin protocol because it is cryptographically secure. This allows the core of Bitcoin to be trusted for being completely neutral, transparent and predictable.</li></ul></p></div><p class="faq_question bold">What are the disadvantages of Bitcoin?</p><div class="faq_answer"><p><ul style="text-align:left;"><li>Degree of acceptance - Many people are still unaware of Bitcoin. Every day, more businesses accept bitcoins because they want the advantages of doing so, but the list remains small and still needs to grow in order to benefit from network effects.</li><li>Volatility - The total value of bitcoins in circulation and the number of businesses using Bitcoin are still very small compared to what they could be. Therefore, relatively small events, trades, or business activities can significantly affect the price. In theory, this volatility will decrease as Bitcoin markets and the technology matures. Never before has the world seen a start-up currency, so it is truly difficult (and exciting) to imagine how it will play out.</li><li>Ongoing development - Bitcoin software is still in beta with many incomplete features in active development. New tools, features, and services are being developed to make Bitcoin more secure and accessible to the masses. Some of these are still not ready for everyone. Most Bitcoin businesses are new and still offer no insurance. In general, Bitcoin is still in the process of maturing.</li></ul></p></div><p class="faq_question bold">Why do people trust Bitcoin?</p><div class="faq_answer"><p>Much of the trust in Bitcoin comes from the fact that it requires no trust at all. Bitcoin is fully open-source and decentralized. This means that anyone has access to the entire source code at any time. Any developer in the world can therefore verify exactly how Bitcoin works. All transactions and bitcoins issued into existence can be transparently consulted in real-time by anyone. All payments can be made without reliance on a third party and the whole system is protected by heavily peer-reviewed cryptographic algorithms like those used for online banking. No organization or individual can control Bitcoin, and the network remains secure even if not all of its users can be trusted.</p></div><p class="faq_question bold">Can I make money with Bitcoin?</p><div class="faq_answer"><p>You should never expect to get rich with Bitcoin or any emerging technology. It is always important to be wary of anything that sounds too good to be true or disobeys basic economic rules.</p><p>Bitcoin is a growing space of innovation and there are business opportunities that also include risks. There is no guarantee that Bitcoin will continue to grow even though it has developed at a very fast rate so far. Investing time and resources on anything related to Bitcoin requires entrepreneurship. There are various ways to make money with Bitcoin such as mining, speculation or running new businesses. All of these methods are competitive and there is no guarantee of profit. It is up to each individual to make a proper evaluation of the costs and the risks involved in any such project.</p></div><p class="faq_question bold">Is Bitcoin fully virtual and immaterial?</p><div class="faq_answer"><p>Bitcoin is as virtual as the credit cards and online banking networks people use everyday. Bitcoin can be used to pay online and in physical stores just like any other form of money. Bitcoins can also be exchanged in physical form such as the Casascius coins, but paying with a mobile phone usually remains more convenient. Bitcoin balances are stored in a large distributed network, and they cannot be fraudulently altered by anybody. In other words, Bitcoin users have exclusive control over their funds and bitcoins cannot vanish just because they are virtual.</p></div><p class="faq_question bold">Is Bitcoin anonymous?</p><div class="faq_answer"><p>Bitcoin is designed to allow its users to send and receive payments with an acceptable level of privacy as well as any other form of money. However, Bitcoin is not anonymous and cannot offer the same level of privacy as cash. The use of Bitcoin leaves extensive public records. Various mechanisms exist to protect users&rsquo; privacy, and more are in development. However, there is still work to be done before these features are used correctly by most Bitcoin users.</p><p>Some concerns have been raised that private transactions could be used for illegal purposes with Bitcoin. However, it is worth noting that Bitcoin will undoubtedly be subjected to similar regulations that are already in place inside existing financial systems. Bitcoin cannot be more anonymous than cash and it is not likely to prevent criminal investigations from being conducted. Additionally, Bitcoin is also designed to prevent a large range of financial crimes.</p></div><p class="faq_question bold">What happens when bitcoins are lost?</p><div class="faq_answer"><p>When a user loses his wallet, it has the effect of removing money out of circulation. Lost bitcoins still remain in the block chain just like any other bitcoins. However, lost bitcoins remain dormant forever because there is no way for anybody to find the private key(s) that would allow them to be spent again. Because of the law of supply and demand, when fewer bitcoins are available, the ones that are left will be in higher demand and increase in value to compensate.</p></div><p class="faq_question bold">Can Bitcoin scale to become a major payment network?</p><div class="faq_answer"><p>The Bitcoin network can already process a much higher number of transactions per second than it does today. It is, however, not entirely ready to scale to the level of major credit card networks. Work is underway to lift current limitations, and future requirements are well known. Since inception, every aspect of the Bitcoin network has been in a continuous process of maturation, optimization, and specialization, and it should be expected to remain that way for some years to come. As traffic grows, more Bitcoin users may use lightweight clients, and full network nodes may become a more specialized service. For more details, see the Scalability page on the Wiki.</p></div><p class="faq_question bold">Is Bitcoin legal?</p><div class="faq_answer"><p>To the best of our knowledge, Bitcoin has not been made illegal by legislation in most jurisdictions. However, some jurisdictions (such as Argentina and Russia) severely restrict or ban foreign currencies. Other jurisdictions (such as Thailand) may limit the licensing of certain entities such as Bitcoin exchanges.</p><p>Regulators from various jurisdictions are taking steps to provide individuals and businesses with rules on how to integrate this new technology with the formal, regulated financial system. For example, the Financial Crimes Enforcement Network (FinCEN), a bureau in the United States Treasury Department, issued non-binding guidance on how it characterizes certain activities involving virtual currencies.</p></div><p class="faq_question bold">Is Bitcoin useful for illegal activities?</p><div class="faq_answer"><p>Bitcoin is money, and money has always been used both for legal and illegal purposes. Cash, credit cards and current banking systems widely surpass Bitcoin in terms of their use to finance crime. Bitcoin can bring significant innovation in payment systems and the benefits of such innovation are often considered to be far beyond their potential drawbacks.</p><p>Bitcoin is designed to be a huge step forward in making money more secure and could also act as a significant protection against many forms of financial crime. For instance, bitcoins are completely impossible to counterfeit. Users are in full control of their payments and cannot receive unapproved charges such as with credit card fraud. Bitcoin transactions are irreversible and immune to fraudulent chargebacks. Bitcoin allows money to be secured against theft and loss using very strong and useful mechanisms such as backups, encryption, and multiple signatures.</p><p>Some concerns have been raised that Bitcoin could be more attractive to criminals because it can be used to make private and irreversible payments. However, these features already exist with cash and wire transfer, which are widely used and well-established. The use of Bitcoin will undoubtedly be subjected to similar regulations that are already in place inside existing financial systems, and Bitcoin is not likely to prevent criminal investigations from being conducted. In general, it is common for important breakthroughs to be perceived as being controversial before their benefits are well understood. The Internet is a good example among many others to illustrate this.</p></div><p class="faq_question bold">Can Bitcoin be regulated?</p><div class="faq_answer"><p>The Bitcoin protocol itself cannot be modified without the cooperation of nearly all its users, who choose what software they use. Attempting to assign special rights to a local authority in the rules of the global Bitcoin network is not a practical possibility. Any rich organization could choose to invest in mining hardware to control half of the computing power of the network and become able to block or reverse recent transactions. However, there is no guarantee that they could retain this power since this requires to invest as much than all other miners in the world.</p><p>It is however possible to regulate the use of Bitcoin in a similar way to any other instrument. Just like the dollar, Bitcoin can be used for a wide variety of purposes, some of which can be considered legitimate or not as per each jurisdiction&rsquo;s laws. In this regard, Bitcoin is no different than any other tool or resource and can be subjected to different regulations in each country. Bitcoin use could also be made difficult by restrictive regulations, in which case it is hard to determine what percentage of users would keep using the technology. A government that chooses to ban Bitcoin would prevent domestic businesses and markets from developing, shifting innovation to other countries. The challenge for regulators, as always, is to develop efficient solutions while not impairing the growth of new emerging markets and businesses.</p></div><p class="faq_question bold">What about Bitcoin and taxes?</p><div class="faq_answer"><p>Bitcoin is not a fiat currency with legal tender status in any jurisdiction, but often tax liability accrues regardless of the medium used. There is a wide variety of legislation in many different jurisdictions which could cause income, sales, payroll, capital gains, or some other form of tax liability to arise with Bitcoin.</p></div><p class="faq_question bold">What about Bitcoin and consumer protection?</p><div class="faq_answer"><p>Bitcoin is freeing people to transact on their own terms. Each user can send and receive payments in a similar way to cash but they can also take part in more complex contracts. Multiple signatures allow a transaction to be accepted by the network only if a certain number of a defined group of persons agree to sign the transaction. This allows innovative dispute mediation services to be developed in the future. Such services could allow a third party to approve or reject a transaction in case of disagreement between the other parties without having control on their money. As opposed to cash and other payment methods, Bitcoin always leaves a public proof that a transaction did take place, which can potentially be used in a recourse against businesses with fraudulent practices.</p><p>It is also worth noting that while merchants usually depend on their public reputation to remain in business and pay their employees, they don&rsquo;t have access to the same level of information when dealing with new consumers. The way Bitcoin works allows both individuals and businesses to be protected against fraudulent chargebacks while giving the choice to the consumer to ask for more protection when they are not willing to trust a particular merchant.</p></div><p class="faq_question bold">How are bitcoins created?</p><div class="faq_answer"><p>New bitcoins are generated by a competitive and decentralized process called "mining". This process involves that individuals are rewarded by the network for their services. Bitcoin miners are processing transactions and securing the network using specialized hardware and are collecting new bitcoins in exchange.</p><p>The Bitcoin protocol is designed in such a way that new bitcoins are created at a fixed rate. This makes Bitcoin mining a very competitive business. When more miners join the network, it becomes increasingly difficult to make a profit and miners must seek efficiency to cut their operating costs. No central authority or developer has any power to control or manipulate the system to increase their profits. Every Bitcoin node in the world will reject anything that does not comply with the rules it expects the system to follow.</p><p>Bitcoins are created at a decreasing and predictable rate. The number of new bitcoins created each year is automatically halved over time until bitcoin issuance halts completely with a total of 21 million bitcoins in existence. At this point, Bitcoin miners will probably be supported exclusively by numerous small transaction fees.</p></div><p class="faq_question bold">Why do bitcoins have value?</p><div class="faq_answer"><p>Bitcoins have value because they are useful as a form of money. Bitcoin has the characteristics of money (durability, portability, fungibility, scarcity, divisibility, and recognizability) based on the properties of mathematics rather than relying on physical properties (like gold and silver) or trust in central authorities (like fiat currencies). In short, Bitcoin is backed by mathematics. With these attributes, all that is required for a form of money to hold value is trust and adoption. In the case of Bitcoin, this can be measured by its growing base of users, merchants, and startups. As with all currency, bitcoin&rsquo;s value comes only and directly from people willing to accept them as payment.</p></div><p class="faq_question bold">What determines bitcoin&rsquo;s price?</p><div class="faq_answer"><p>The price of a bitcoin is determined by supply and demand. When demand for bitcoins increases, the <a href="https://freebitco.in/site/bitcoin/" target="blank">Bitcoin price</a> increases, and when demand falls, the price falls. There is only a limited number of bitcoins in circulation and new bitcoins are created at a predictable and decreasing rate, which means that demand must follow this level of inflation to keep the price stable. Because Bitcoin is still a relatively small market compared to what it could be, it doesn&rsquo;t take significant amounts of money to move the market price up or down, and thus the price of a bitcoin is still very volatile.</p></div><p class="faq_question bold">Can bitcoins become worthless?</p><div class="faq_answer"><p>Yes. History is littered with currencies that failed and are no longer used, such as the German Mark during the Weimar Republic and, more recently, the Zimbabwean dollar. Although previous currency failures were typically due to hyperinflation of a kind that Bitcoin makes impossible, there is always potential for technical failures, competing currencies, political issues and so on. As a basic rule of thumb, no currency should be considered absolutely safe from failures or hard times. Bitcoin has proven reliable for years since its inception and there is a lot of potential for Bitcoin to continue to grow. However, no one is in a position to predict what the future will be for Bitcoin.</p></div><p class="faq_question bold">Is Bitcoin a bubble?</p><div class="faq_answer"><p>A fast rise in price does not constitute a bubble. An artificial over-valuation that will lead to a sudden downward correction constitutes a bubble. Choices based on individual human action by hundreds of thousands of market participants is the cause for bitcoin&rsquo;s price to fluctuate as the market seeks price discovery. Reasons for changes in sentiment may include a loss of confidence in Bitcoin, a large difference between value and price not based on the fundamentals of the Bitcoin economy, increased press coverage stimulating speculative demand, fear of uncertainty, and old-fashioned irrational exuberance and greed.</p></div><p class="faq_question bold">Is Bitcoin a Ponzi scheme?</p><div class="faq_answer"><p>A Ponzi scheme is a fraudulent investment operation that pays returns to its investors from their own money, or the money paid by subsequent investors, instead of from profit earned by the individuals running the business. Ponzi schemes are designed to collapse at the expense of the last investors when there is not enough new participants.</p><p>Bitcoin is a free software project with no central authority. Consequently, no one is in a position to make fraudulent representations about investment returns. Like other major currencies such as gold, United States dollar, euro, yen, etc. there is no guaranteed purchasing power and the exchange rate floats freely. This leads to volatility where owners of bitcoins can unpredictably make or lose money. Beyond speculation, Bitcoin is also a payment system with useful and competitive attributes that are being used by thousands of users and businesses.</p></div><p class="faq_question bold">Doesn&rsquo;t Bitcoin unfairly benefit early adopters?</p><div class="faq_answer"><p>Some early adopters have large numbers of bitcoins because they took risks and invested time and resources in an unproven technology that was hardly used by anyone and that was much harder to secure properly. Many early adopters spent large numbers of bitcoins quite a few times before they became valuable or bought only small amounts and didn&rsquo;t make huge gains. There is no guarantee that the price of a bitcoin will increase or drop. This is very similar to investing in an early startup that can either gain value through its usefulness and popularity, or just never break through. Bitcoin is still in its infancy, and it has been designed with a very long-term view; it is hard to imagine how it could be less biased towards early adopters, and today&rsquo;s users may or may not be the early adopters of tomorrow.</p></div><p class="faq_question bold">Won&rsquo;t the finite amount of bitcoins be a limitation?</p><div class="faq_answer"><p>Bitcoin is unique in that only 21 million bitcoins will ever be created. However, this will never be a limitation because transactions can be denominated in smaller sub-units of a bitcoin, such as bits - there are 1,000,000 bits in 1 bitcoin. Bitcoins can be divided up to 8 decimal places (0.000 000 01) and potentially even smaller units if that is ever required in the future as the average transaction size decreases.</p></div><p class="faq_question bold">Won&rsquo;t Bitcoin fall in a deflationary spiral?</p><div class="faq_answer"><p>The deflationary spiral theory says that if prices are expected to fall, people will move purchases into the future in order to benefit from the lower prices. That fall in demand will in turn cause merchants to lower their prices to try and stimulate demand, making the problem worse and leading to an economic depression.</p><p>Although this theory is a popular way to justify inflation amongst central bankers, it does not appear to always hold true and is considered controversial amongst economists. Consumer electronics is one example of a market where prices constantly fall but which is not in depression. Similarly, the value of bitcoins has risen over time and yet the size of the Bitcoin economy has also grown dramatically along with it. Because both the value of the currency and the size of its economy started at zero in 2009, Bitcoin is a counterexample to the theory showing that it must sometimes be wrong.</p><p>Notwithstanding this, Bitcoin is not designed to be a deflationary currency. It is more accurate to say Bitcoin is intended to inflate in its early years, and become stable in its later years. The only time the quantity of bitcoins in circulation will drop is if people carelessly lose their wallets by failing to make backups. With a stable monetary base and a stable economy, the value of the currency should remain the same.</p></div><p class="faq_question bold">Isn&rsquo;t speculation and volatility a problem for Bitcoin?</p><div class="faq_answer"><p>This is a chicken and egg situation. For bitcoin&rsquo;s price to stabilize, a large scale economy needs to develop with more businesses and users. For a large scale economy to develop, businesses and users will seek for price stability.</p><p>Fortunately, volatility does not affect the main benefits of Bitcoin as a payment system to transfer money from point A to point B. It is possible for businesses to convert bitcoin payments to their local currency instantly, allowing them to profit from the advantages of Bitcoin without being subjected to price fluctuations. Since Bitcoin offers many useful and unique features and properties, many users choose to use Bitcoin. With such solutions and incentives, it is possible that Bitcoin will mature and develop to a degree where price volatility will become limited.</p></div><p class="faq_question bold">What if someone bought up all the existing bitcoins?</p><div class="faq_answer"><p>Only a fraction of bitcoins issued to date are found on the exchange markets for sale. Bitcoin markets are competitive, meaning the price of a bitcoin will rise or fall depending on supply and demand. Additionally, new bitcoins will continue to be issued for decades to come. Therefore even the most determined buyer could not buy all the bitcoins in existence. This situation isn&rsquo;t to suggest, however, that the markets aren&rsquo;t vulnerable to price manipulation; it still doesn&rsquo;t take significant amounts of money to move the market price up or down, and thus Bitcoin remains a volatile asset thus far.</p></div><p class="faq_question bold">What if someone creates a better digital currency?</p><div class="faq_answer"><p>That can happen. For now, Bitcoin remains by far the most popular decentralized virtual currency, but there can be no guarantee that it will retain that position. There is already a set of alternative currencies inspired by Bitcoin. It is however probably correct to assume that significant improvements would be required for a new currency to overtake Bitcoin in terms of established market, even though this remains unpredictable. Bitcoin could also conceivably adopt improvements of a competing currency so long as it doesn&rsquo;t change fundamental parts of the protocol.</p></div><p class="faq_question bold">What is Bitcoin mining?</p><div class="faq_answer"><p>Mining is the process of spending computing power to process transactions, secure the network, and keep everyone in the system synchronized together. It can be perceived like the Bitcoin data center except that it has been designed to be fully decentralized with miners operating in all countries and no individual having control over the network. This process is referred to as "mining" as an analogy to gold mining because it is also a temporary mechanism used to issue new bitcoins. Unlike gold mining, however, Bitcoin mining provides a reward in exchange for useful services required to operate a secure payment network. Mining will still be required after the last bitcoin is issued.</p></div><p class="faq_question bold">How does Bitcoin mining work?</p><div class="faq_answer"><p>Anybody can become a Bitcoin miner by running software with specialized hardware. Mining software listens for transactions broadcast through the peer-to-peer network and performs appropriate tasks to process and confirm these transactions. Bitcoin miners perform this work because they can earn transaction fees paid by users for faster transaction processing, and newly created bitcoins issued into existence according to a fixed formula.</p><p>For new transactions to be confirmed, they need to be included in a block along with a mathematical proof of work. Such proofs are very hard to generate because there is no way to create them other than by trying billions of calculations per second. This requires miners to perform these calculations before their blocks are accepted by the network and before they are rewarded. As more people start to mine, the difficulty of finding valid blocks is automatically increased by the network to ensure that the average time to find a block remains equal to 10 minutes. As a result, mining is a very competitive business where no individual miner can control what is included in the block chain.</p><p>The proof of work is also designed to depend on the previous block to force a chronological order in the block chain. This makes it exponentially difficult to reverse previous transactions because this requires the recalculation of the proofs of work of all the subsequent blocks. When two blocks are found at the same time, miners work on the first block they receive and switch to the longest chain of blocks as soon as the next block is found. This allows mining to secure and maintain a global consensus based on processing power.</p><p>Bitcoin miners are neither able to cheat by increasing their own reward nor process fraudulent transactions that could corrupt the Bitcoin network because all Bitcoin nodes would reject any block that contains invalid data as per the rules of the Bitcoin protocol. Consequently, the network remains secure even if not all Bitcoin miners can be trusted.</p></div><p class="faq_question bold">Isn&rsquo;t Bitcoin mining a waste of energy?</p><div class="faq_answer"><p>Spending energy to secure and operate a payment system is hardly a waste. Like any other payment service, the use of Bitcoin entails processing costs. Services necessary for the operation of currently widespread monetary systems, such as banks, credit cards, and armored vehicles, also use a lot of energy. Although unlike Bitcoin, their total energy consumption is not transparent and cannot be as easily measured.</p><p>Bitcoin mining has been designed to become more optimized over time with specialized hardware consuming less energy, and the operating costs of mining should continue to be proportional to demand. When Bitcoin mining becomes too competitive and less profitable, some miners choose to stop their activities. Furthermore, all energy expended mining is eventually transformed into heat, and the most profitable miners will be those who have put this heat to good use. An optimally efficient mining network is one that isn&rsquo;t actually consuming any extra energy. While this is an ideal, the economics of mining are such that miners individually strive toward it.</p></div><p class="faq_question bold">How does mining help secure Bitcoin?</p><div class="faq_answer"><p>Mining creates the equivalent of a competitive lottery that makes it very difficult for anyone to consecutively add new blocks of transactions into the block chain. This protects the neutrality of the network by preventing any individual from gaining the power to block certain transactions. This also prevents any individual from replacing parts of the block chain to roll back their own spends, which could be used to defraud other users. Mining makes it exponentially more difficult to reverse a past transaction by requiring the rewriting of all blocks following this transaction.</p></div><p class="faq_question bold">What do I need to start mining?</p><div class="faq_answer"><p>In the early days of Bitcoin, anyone could find a new block using their computer&rsquo;s CPU. As more and more people started mining, the difficulty of finding new blocks increased greatly to the point where the only cost-effective method of mining today is using specialized hardware. You can visit BitcoinMining.com for more information.</p></div><p class="faq_question bold">Is Bitcoin secure?</p><div class="faq_answer"><p>The Bitcoin technology - the protocol and the cryptography - has a strong security track record, and the Bitcoin network is probably the biggest distributed computing project in the world. Bitcoin&rsquo;s most common vulnerability is in user error. Bitcoin wallet files that store the necessary private keys can be accidentally deleted, lost or stolen. This is pretty similar to physical cash stored in a digital form. Fortunately, users can employ sound security practices to protect their money or use service providers that offer good levels of security and insurance against theft or loss.</p></div><p class="faq_question bold">Hasn&rsquo;t Bitcoin been hacked in the past?</p><div class="faq_answer"><p>The rules of the protocol and the cryptography used for Bitcoin are still working years after its inception, which is a good indication that the concept is well designed. However, security flaws have been found and fixed over time in various software implementations. Like any other form of software, the security of Bitcoin software depends on the speed with which problems are found and fixed. The more such issues are discovered, the more Bitcoin is gaining maturity.</p><p>There are often misconceptions about thefts and security breaches that happened on diverse exchanges and businesses. Although these events are unfortunate, none of them involve Bitcoin itself being hacked, nor imply inherent flaws in Bitcoin; just like a bank robbery doesn&rsquo;t mean that the dollar is compromised. However, it is accurate to say that a complete set of good practices and intuitive security solutions is needed to give users better protection of their money, and to reduce the general risk of theft and loss. Over the course of the last few years, such security features have quickly developed, such as wallet encryption, offline wallets, hardware wallets, and multi-signature transactions.</p></div><p class="faq_question bold">Could users collude against Bitcoin?</p><div class="faq_answer"><p>It is not possible to change the Bitcoin protocol that easily. Any Bitcoin client that doesn&rsquo;t comply with the same rules cannot enforce their own rules on other users. As per the current specification, double spending is not possible on the same block chain, and neither is spending bitcoins without a valid signature. Therefore, It is not possible to generate uncontrolled amounts of bitcoins out of thin air, spend other users&rsquo; funds, corrupt the network, or anything similar.</p><p>However, powerful miners could arbitrarily choose to block or reverse recent transactions. A majority of users can also put pressure for some changes to be adopted. Because Bitcoin only works correctly with a complete consensus between all users, changing the protocol can be very difficult and requires an overwhelming majority of users to adopt the changes in such a way that remaining users have nearly no choice but to follow. As a general rule, it is hard to imagine why any Bitcoin user would choose to adopt any change that could compromise their own money.</p></div><p class="faq_question bold">Is Bitcoin vulnerable to quantum computing?</p><div class="faq_answer"><p>Yes, most systems relying on cryptography in general are, including traditional banking systems. However, quantum computers don&rsquo;t yet exist and probably won&rsquo;t for a while. In the event that quantum computing could be an imminent threat to Bitcoin, the protocol could be upgraded to use post-quantum algorithms. Given the importance that this update would have, it can be safely expected that it would be highly reviewed by developers and adopted by all Bitcoin users.</p></div><p class="faq_question bold">What if I have more questions about Bitcoin?</p><div class="faq_answer"><p>Three great places where you can get your questions answered are the BitcoinTalk Forum at <a href="http://bitcointalk.org" target="_blank">BitcoinTalk.org</a> and Bitcoin Stack Exchange at <a href="http://bitcoin.stackexchange.com/" target="_blank">Bitcoin.StackExchange.com</a>.</p></div>');
}
function insertIntoBetHistory(outcome, win_amount, lucky_number, server_seed, client_seed, sseed_hash, nonce, game_type, bet_on, jackpot, stake, multiplier, balance_before, balance_after, bonus_balance_before, bonus_balance_after, time) {
    if (outcome === "w") {
        win_amount = "<font color=green>" + win_amount + "</font>";
    } else if (outcome === "l") {
        win_amount = "<font color=red>-" + win_amount + "</font>";
    }
    bet_on = bet_on.toUpperCase();
    if (jackpot == "0") {
        jackpot = "&#x2716";
    } else if (jackpot) {
        var result3 = jackpot.split(",");
        var jackpot_string = "";
        for (var z = 0; z < result3.length; z++) {
            jackpot_string = jackpot_string + jackpot_costs[result3[z]] + " | "
        }
        jackpot_string = jackpot_string.slice(0, -3);
        jackpot = "<span data-tooltip class='has-tip' title='" + jackpot_string + "' style='cursor:pointer;'>&#x2714</span>";
    } else {
        jackpot = "&nbsp;"
    }
    if (bet_on == "") {
        bet_on = "&nbsp;"
    }
    if ($('div.multiply_bet_history_table_row').length >= 20) {
        $('div.multiply_bet_history_table_row').last().remove();
    }
    var date = formatDate();
    if (time) {
        date = time;
    }
    var split_date = date.split(" ");
    var row_html = '<div class="multiply_bet_history_table_row"><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-2 small-2 columns center lottery_winner_table_box lottery_winner_table_first_last_cell"><i class="show_balance_before_after fa fa-arrows-alt" aria-hidden="true"></i>' + split_date[1] + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + game_type + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + bet_on + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + lucky_number + '</div><div class="large-2 small-2 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + stake + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + multiplier + '</div><div class="large-2 small-2 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + win_amount + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_third_cell">' + jackpot + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell"><a href="https://s3.amazonaws.com/roll-verifier/verify.html?server_seed=' + server_seed + '&client_seed=' + client_seed + '&server_seed_hash=' + sseed_hash + '&nonce=' + nonce + '" target=_blank>CLICK</a></div></div><div class="balance_before_after" class="large-12 small-12 columns center lottery_winner_table_box_container effect2" style="display: none;"><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell bb_background" style="font-weight: bold;">BB&nbsp; &nbsp;<i class="fa fa-info-circle" aria-hidden="true" title="Account Balance Before Bet"></i><span class="arrow-up balance_after_bet_span_1"></span><span class="arrow-up-small balance_after_bet_span_2"></span></div><div class="large-2 small-2 columns center lottery_winner_table_box balance_after_bet_column bb_background">' + balance_before + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell bb_background" style="font-weight: bold;">BA&nbsp; &nbsp;<i class="fa fa-info-circle" aria-hidden="true" title="Account Balance After Bet"></i><span class="arrow-up balance_after_bet_span_1"></span><span class="arrow-up-small balance_after_bet_span_2"></span></div><div class="large-2 small-2 columns center lottery_winner_table_box balance_after_bet_column bb_background">' + balance_after + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell bb_background" style="font-weight: bold;">BAB&nbsp;<i class="fa fa-info-circle" aria-hidden="true" title="Bonus Account Balance Before Bet"></i><span class="arrow-up balance_after_bet_span_1"></span><span class="arrow-up-small balance_after_bet_span_2"></span></div><div class="large-2 small-2 columns center lottery_winner_table_box balance_after_bet_column bb_background">' + bonus_balance_before + '</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell bb_background" style="font-weight: bold;">BAA&nbsp;<i class="fa fa-info-circle" aria-hidden="true" title="Bonus Account Balance After Bet"></i><span class="arrow-up balance_after_bet_span_1"></span><span class="arrow-up-small balance_after_bet_span_2"></span></div><div class="large-2 small-2 columns center lottery_winner_table_box balance_after_bet_last_column bb_background">' + bonus_balance_after + '</div></div></div>';
    var bet_history_date = split_date[0];
    var date_row_name = bet_history_date.replace(/\//g, '_');
    if ($('#multiply_history_date_row_' + date_row_name).length == 0) {
        row_html = '<div class="large-12 small-12 columns center lottery_winner_table_box" id="multiply_history_date_row_' + date_row_name + '"><div class="center" style="margin:auto; font-weight:bold;">DATE: ' + split_date[0] + '</div></div> <div class="large-12 small-12 columns center lottery_winner_table_box_container effect2 multiply_history_table_header"><div class="large-2 small-2 columns center lottery_winner_table_box lottery_winner_table_first_last_cell font_bold">TIME</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">GAME</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">BET</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">ROLL</div><div class="large-2 small-2 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">STAKE</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold"><span data-tooltip class="has-tip" title="Multiplier">MULT</span></div><div class="large-2 small-2 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">PROFIT</div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_third_cell font_bold"><span data-tooltip class="has-tip" title="Jackpot">JPOT</span></div><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell font_bold"><span data-tooltip class="has-tip" title="Verify">VER</span></div></div>' + row_html;
        $('#bet_history_table_rows').prepend(row_html);
    } else {
        $('#multiply_history_date_row_' + date_row_name).next('div.multiply_history_table_header').after(row_html);
    }
    return false;
}
function VisitLink(url) {
    if (url != "none") {
        window.open(url)
    }
}
function RedeemRPProduct(id) {
    $(".reward_link_redeem_button_style").attr("disabled", true);
    $(".orange_button").attr("disabled", true);
    var points = $("#encash_points_number").val();
    $.get('/?op=redeem_rewards&id=' + id + '&points=' + points, function(data) {
        var result = data.split(":");
        var msg;
        if (result[0] == "s") {
            $('.user_reward_points').html(result[2]);
            if (result[1] == "s1") {
                $('#balance').html(result[5]);
                balanceChanged();
                msg = "Successfully converted " + ReplaceNumberWithCommas(parseInt(result[3])) + " points to " + parseFloat(parseInt(result[4]) / 100000000).toFixed(8) + "BTC.";
            } else if (result[1] == "s2") {
                msg = "Your bonus has been succesfully activated!";
                var inner_div_html = '<p>Active bonus <span class="free_play_bonus_box_span_large">' + result[5] + '</span> ends in <span class="free_play_bonus_box_span_large" id="bonus_span_' + result[3] + '"></span></p>';
                if ($("#bonus_container_" + result[3]).length > 0) {
                    $("#bonus_container_" + result[3]).html(inner_div_html);
                } else {
                    $('#reward_points_bonuses_main_div').append('<div class="bold center free_play_bonus_box_large" id="bonus_container_' + result[3] + '">' + inner_div_html + '</div>');
                }
                $("#bonus_container_" + result[3]).show();
                BonusEndCountdown(result[3], parseInt(result[4]));
                if (result[3] == "fp_bonus") {
                    $('#fp_min_reward').html(result[6] + " BTC");
                }
            } else if (result[1] == "s3") {
                msg = "Your redemption request for " + result[3] + " has been sent succesfully. We shall contact you via email for your shipping details (if required). If you do not have an email address added to your account, please add it now via the PROFILE page.";
            }
        } else if (result[0] == "e") {
            msg = result[1];
        }
        DisplaySEMessage(result[0], msg);
        $(".reward_link_redeem_button_style").attr("disabled", false);
        $(".orange_button").attr("disabled", false);
    });
}
function BonusEndCountdown(selector, duration) {
    var start = Date.now(), diff, hours, minutes, seconds;
    var timer_run = setInterval(function timer() {
        diff = duration - (((Date.now() - start) / 1000) | 0);
        hours = (diff / (60 * 60)) | 0;
        minutes = ((diff - (hours * 60 * 60)) / 60) | 0;
        seconds = (diff - (minutes * 60) - (hours * 60 * 60)) | 0;
        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        $("#bonus_span_" + selector).html(hours + 'h:' + minutes + 'm:' + seconds + 's');
        if (diff <= 0) {
            $("#bonus_container_" + selector).hide();
            clearInterval(timer_run);
            return;
        }
    }, 1000);
}
function DisplaySEMessage(result, msg, custom_timeout) {
    if (result != '' && result != 0 && result != undefined && msg != '' && msg != 0 && msg != undefined) {
        clearTimeout(se_msg_timeout_id);
        $('.reward_point_redeem_result_box').removeClass('reward_point_redeem_result_error');
        $('.reward_point_redeem_result_box').removeClass('reward_point_redeem_result_success');
        $('#reward_point_redeem_result_container_div').show();
        if (result == "s") {
            $('.reward_point_redeem_result_box').addClass('reward_point_redeem_result_success');
        } else if (result == "e") {
            $('.reward_point_redeem_result_box').addClass('reward_point_redeem_result_error');
        }
        $('.reward_point_redeem_result').html(msg);
        var timeout = 15000;
        if (custom_timeout > 0) {
            timeout = custom_timeout;
        }
        se_msg_timeout_id = setTimeout(function() {
            $('#reward_point_redeem_result_container_div').hide();
        }, timeout);
    }
}
function balanceChanged() {
    if (max_deposit_bonus > parseFloat(min_bonus_amount)) {
        $('.dep_bonus_max').html(max_deposit_bonus + " BTC");
    }
    $('#balance2').html($('#balance').html());
    $('#balance_small').html($('#balance').html());
    balance_last_changed = Math.floor(Date.now() / 1000);
}
function GenerateHashes(string) {
    var rand = Math.random();
    var hash = CryptoJS.SHA1(rand.toString() + rand.toString()).toString(CryptoJS.enc.Hex);
    while (hash.indexOf(string) == -1) {
        rand = Math.random();
        hash = CryptoJS.SHA1(rand.toString() + rand.toString()).toString(CryptoJS.enc.Hex);
        if (hash.indexOf(string) !== -1) {
            return rand;
        }
    }
}
function Reset2FAQuestions(response) {
    if (response == "secret_key_yes") {
        $('#reset_2fa_form_full').show();
        $('#reset_2fa_subtype').val('secret_key');
        $('#forgot_2fa_secret_key_container_div').show();
        $('#forgot_2fa_extra_input').val('');
        $('#forgot_2fa_extra_field').html('SECRET KEY');
    } else if (response == "secret_key_no") {
        $('#forgot_2fa_secret_key_container_div').hide();
        $('#reset_2fa_form_full').hide();
        $('#forgot_2fa_question_text').html('Did you provide a backup phone number for resetting your 2FA?');
        $('#forgot_2fa_yes').attr("onclick", "Reset2FAQuestions('mobile_ver_yes')");
        $('#forgot_2fa_no').attr("onclick", "Reset2FAQuestions('mobile_ver_no')");
    } else if (response == "mobile_ver_yes") {
        $('#forgot_2fa_secret_key_container_div').hide();
        $('#reset_2fa_form_full').show();
        $('#reset_2fa_subtype').val('mobile_ver');
        $('#forgot_2fa_extra_input').val('');
        $('#reset_2fa_warning').show();
        $('#reset_2fa_warning').html('There is a small charge of $0.15 for this process that will be applied to your account after your 2FA has been reset. This charge is to cover our costs for sending the verification code to your phone.');
    } else if (response == "mobile_ver_no") {
        $('#reset_2fa_form_full').show();
        $('#reset_2fa_subtype').val('email_ver');
        $('#forgot_2fa_secret_key_container_div').hide();
        $('#forgot_2fa_extra_input').val('');
        $('#reset_2fa_warning').show();
        $('#reset_2fa_warning').html('This procedure will take approximately 8 days to reset your 2FA. If you are able to use another way to reset your 2FA, please use that first.');
    } else if (response == "start_over") {
        $('#reset_2fa_form_full').hide();
        $('#reset_2fa_subtype').val('');
        $('#forgot_2fa_extra_input').val('');
        $('#reset_2fa_warning').hide();
        $('#forgot_2fa_yes').attr("onclick", "Reset2FAQuestions('secret_key_yes')");
        $('#forgot_2fa_no').attr("onclick", "Reset2FAQuestions('secret_key_no')");
        $('#forgot_2fa_question_text').html('DO YOU HAVE THE 2FA SECRET KEY?');
        $('#forgot_2fa_secret_key_container_div').hide();
    }
}
function PlayCaptchasNetAudioCaptcha(captcha_random) {
    var audioElement = document.createElement('audio');
    audioElement.setAttribute('src', '//captchas.freebitco.in/cgi-bin/mp3/index.cgi?client=freebitcoin&random=' + captcha_random);
    audioElement.setAttribute('autoplay', 'autoplay');
    audioElement.addEventListener("load", function() {
        audioElement.play();
    }, true);
}
function SwitchCaptchas(type) {
    var other_type = "recaptcha";
    if (type == "recaptcha") {
        other_type = "double_captchas";
    }
    $("#free_play_" + other_type).hide();
    $("#free_play_" + type).show();
    $("#signup_" + other_type).hide();
    $("#signup_" + type).show();
    $('#switch_captchas_button').attr("onclick", "SwitchCaptchas('" + other_type + "')");
    $.cookie.raw = true;
    $.cookie('default_captcha', type, {
        expires: 3650,
        secure: true
    });
}
function CountupTimer(selector_name, final_number, runtime, decimals) {
    var si_key = selector_name;
    if (typeof selector_name != 'undefined') {
        si_key = si_key.replace('.', '');
        si_key = si_key.replace('#', '');
    }
    if (typeof countup_setintervals[si_key] != 'undefined') {
        clearInterval(countup_setintervals[si_key]);
        delete countup_setintervals[si_key];
    }
    var final_value = parseFloat(final_number);
    if (isNaN(decimals)) {
        decimals = 8;
    }
    var min_increment = 0.00000001;
    if (final_value > 1) {
        min_increment = min_increment * final_value;
    }
    var timer_interval = parseInt(((-100) * (Math.log10(final_value))) + 300);
    if (timer_interval > 1000) {
        timer_interval = 1000;
    } else if (timer_interval < 100) {
        timer_interval = 100;
    }
    var iter = (runtime * 60 * 1000) / timer_interval;
    var current_value = final_value - (min_increment * iter);
    if (current_value < 0) {
        current_value = 0;
    }
    $(selector_name).html(ReplaceNumberWithCommas(parseFloat(current_value).toFixed(decimals)));
    var diff = final_value - current_value;
    if (diff > 0) {
        countup_setintervals[si_key] = setInterval(function() {
            current_value = current_value + min_increment;
            if (current_value >= final_value) {
                $(selector_name).html(ReplaceNumberWithCommas(parseFloat(final_value).toFixed(decimals)));
                if (typeof countup_setintervals[si_key] != 'undefined') {
                    clearInterval(countup_setintervals[si_key]);
                    delete countup_setintervals[si_key];
                }
            } else {
                if (current_value > 999) {
                    $(selector_name).html(ReplaceNumberWithCommas(parseFloat(current_value).toFixed(decimals)));
                } else {
                    $(selector_name).html(parseFloat(current_value).toFixed(decimals));
                }
            }
        }, timer_interval);
    }
}
function UpdateStats() {
    var update_interval = 15;
    $.get('/cf_stats_public/?f=updating2', function(data) {
        if (typeof data != 'undefined') {
            if (data.status == "success") {
                var epoch_time = Math.floor((new Date).getTime() / 1000);
                if (data.total_btc_won_number > 0) {
                    CountupTimer("#total_btc_won_number", data.total_btc_won_number, update_interval, 8);
                    CountupTimer("#total_btc_won_number_signup_page", data.total_btc_won_number, update_interval, 8);
                }
                if (data.total_plays_number > 0) {
                    CountupTimer("#total_plays_number", data.total_plays_number, update_interval, 0);
                }
                if (data.total_signups_number > 0) {
                    CountupTimer("#total_signups_number", data.total_signups_number, update_interval, 0);
                }
                if (data.total_wagered_number > 0) {
                    CountupTimer("#total_wagered_number", data.total_wagered_number, update_interval, 8);
                }
                if (data.btc_price > 0) {
                    $("#btc_usd_price").html("$" + ReplaceNumberWithCommas(parseFloat(data.btc_price * 100 / 100).toFixed(2)));
                }
                if (data.lambo_lottery_seed_hash.length > 0) {
                    $('#lambo_lottery_hash').val(data.lambo_lottery_seed_hash);
                }
                if (data.current_lottery_round > 0) {
                    $(".current_lottery_round").html(ReplaceNumberWithCommas(parseInt(data.current_lottery_round)));
                }
                if (data.lottery_prize_amount >= 0) {
                    CountupTimer(".lottery_first_prize", parseFloat(data.lottery_prize_amount * 512 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_second_prize", parseFloat(data.lottery_prize_amount * 256 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_third_prize", parseFloat(data.lottery_prize_amount * 128 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_fourth_prize", parseFloat(data.lottery_prize_amount * 64 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_fifth_prize", parseFloat(data.lottery_prize_amount * 32 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_sixth_prize", parseFloat(data.lottery_prize_amount * 16 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_seventh_prize", parseFloat(data.lottery_prize_amount * 8 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_eighth_prize", parseFloat(data.lottery_prize_amount * 4 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_ninth_prize", parseFloat(data.lottery_prize_amount * 2 * 0.000977517 / 100000000), update_interval, 8);
                    CountupTimer(".lottery_tenth_prize", parseFloat(data.lottery_prize_amount * 1 * 0.000977517 / 100000000), update_interval, 8);
                }
                if (data.total_lottery_tickets >= 0) {
                    CountupTimer("#total_lottery_tickets", data.total_lottery_tickets, update_interval, 0);
                    if (typeof $("#user_lottery_tickets").html() != "undefined") {
                        var user_lottery_tickets = parseInt($("#user_lottery_tickets").html().replace(/\,/g, ''));
                        if (user_lottery_tickets >= 0) {
                            $("#lottery_win_chance").html(ReplaceNumberWithCommas(parseFloat(((1 - Math.pow(((data.total_lottery_tickets - user_lottery_tickets) / data.total_lottery_tickets), 10)) * 100) * 100000000 / 100000000).toFixed(8)));
                        } else {
                            $("#lottery_win_chance").html('0.00000000');
                        }
                    }
                }
                if (data.lottery_seed_hash.length > 0) {
                    $("#lottery_seed_hash").val(data.lottery_seed_hash);
                }
                if (data.lottery_ticket_price > 0) {
                    $(".lottery_ticket_price").html(parseFloat(data.lottery_ticket_price / 100000000).toFixed(8));
                }
                if (data.lottery_round_end > 0) {
                    var lottery_timer_end = $('#lottery_round_end').countdown('option', 'until');
                    var d = new Date();
                    var curtime = parseInt(d.getTime() / 1000);
                    var lottery_end_time = curtime + parseInt(lottery_timer_end);
                    if (data.lottery_round_end - lottery_end_time > 29 || lottery_end_time - data.lottery_round_end > 29) {
                        $('#lottery_round_end').countdown('destroy');
                        $('#lottery_round_end').countdown({
                            until: '+' + parseInt(data.lottery_round_end) - curtime,
                            format: 'DHMS'
                        });
                    }
                }
                if (data.lottery_wager.length > 0) {
                    $(".lottery_wager").html(data.lottery_wager);
                }
                if (data.wager_contest.wager.length > 0) {
                    PrintWagerContestTables("wager_promotion_wager_contest", data.wager_contest.wager, "wager");
                }
                if (data.wager_contest.ref_contest.length > 0) {
                    PrintWagerContestTables("wager_promotion_ref_contest", data.wager_contest.ref_contest, "ref");
                }
                if (parseInt(data.wager_contest.contest_end) > 0) {
                    var wager_contest_end = parseInt(data.wager_contest.contest_end);
                    var wager_timer_end = $('#wager_contest_end').countdown('option', 'until');
                    var d = new Date();
                    var curtime = parseInt(d.getTime() / 1000);
                    var wager_end_time = curtime + parseInt(wager_timer_end);
                    if (wager_contest_end - wager_end_time > 29 || wager_end_time - wager_contest_end > 29) {
                        $('#wager_contest_end').countdown('destroy');
                        $('#wager_contest_end').countdown({
                            until: '+' + parseInt(wager_contest_end) - curtime,
                            format: 'DHMS'
                        });
                    }
                }
                if (typeof data.deposit_promo_ends !== 'undefined' && data.deposit_promo_ends > 0 && data.deposit_promo_ends > epoch_time) {
                    var img_link = "https://sirv.freebitco.in/1566195159_1x3KE8We.png";
                    if ($('body').innerWidth() < 769) {
                        if (new_user_first_load == 0) {
                            $("#deposit_promo_message_mobile").show();
                        }
                    } else {
                        img_link = "https://sirv.freebitco.in/1559720837_9AxcLIDY.png";
                        if (new_user_first_load == 0) {
                            $("#deposit_promo_message_regular").show();
                        }
                    }
                    $(".deposit_promo_message_content").html('<div style="display: inline-block; height: auto;" class="center deposit_promo_msg"><img src="' + img_link + '" data-reveal-id="myModal16"><div class="deposit_promo_time_remaining countdown_time_remaining" style="margin: auto; margin-top: 10px; margin-bottom: 10px;"></div></div>');
                    $('.deposit_promo_time_remaining').countdown('destroy');
                    $('.deposit_promo_time_remaining').countdown({
                        until: +(data.deposit_promo_ends - epoch_time),
                        format: 'HMS'
                    });
                    $(".deposit_promo_msg").show();
                } else {
                    var rp_promo_split = data.rp_promo_details.split("-");
                    if (parseInt(rp_promo_split[4]) > epoch_time) {
                        var rp_promo_reward = parseInt(rp_promo_split[0]);
                        var rp_promo_banner_images = {};
                        rp_promo_banner_images["2x"] = "https://sirv.freebitco.in/1565611818_9gS3ptDX.png";
                        rp_promo_banner_images["2x_mobile"] = "https://sirv.freebitco.in/1566044490_iDkTlR09.png";
                        rp_promo_banner_images["3x"] = "https://sirv.freebitco.in/1565611859_WbwN3Uo3.png";
                        rp_promo_banner_images["3x_mobile"] = "https://sirv.freebitco.in/1565948474_K02TT2Kb.png";
                        rp_promo_banner_images["4x"] = "https://sirv.freebitco.in/1565611934_oGczn1PB.png";
                        rp_promo_banner_images["4x_mobile"] = "https://sirv.freebitco.in/1566038802_wPyBX4XO.png";
                        rp_promo_banner_images["5x"] = "https://sirv.freebitco.in/1565611995_trXyTeXE.png";
                        rp_promo_banner_images["5x_mobile"] = "https://sirv.freebitco.in/1566038816_oS89qIoD.png";
                        if (parseInt(rp_promo_split[2]) == 1 && parseInt(rp_promo_split[3]) < epoch_time && parseInt(rp_promo_split[4]) > epoch_time) {
                            var img_link = rp_promo_banner_images[rp_promo_reward + "x_mobile"];
                            if ($('body').innerWidth() < 769) {
                                if (new_user_first_load == 0) {
                                    $("#deposit_promo_message_mobile").show();
                                }
                            } else {
                                img_link = rp_promo_banner_images[rp_promo_reward + "x"];
                                if (new_user_first_load == 0) {
                                    $("#deposit_promo_message_regular").show();
                                }
                            }
                            $(".deposit_promo_message_content").html('<div style="display: inline-block; height: auto;" class="center deposit_promo_msg"><img src="' + img_link + '" onclick="SwitchPageTabs(\x27double_your_btc\x27);"><div class="deposit_promo_time_remaining countdown_time_remaining" style="margin: auto; margin-top: 10px; margin-bottom: 10px;"></div></div>');
                            $('.deposit_promo_time_remaining').countdown('destroy');
                            $('.deposit_promo_time_remaining').countdown({
                                until: +(parseInt(rp_promo_split[4]) - epoch_time),
                                format: 'HMS'
                            });
                        }
                    } else {
                        $(".deposit_promo_msg").hide();
                        $("#deposit_promo_message_mobile").hide();
                        $("#deposit_promo_message_regular").hide();
                    }
                }
                for (var i = 0; i < data.parimutuel_events.length; i++) {
                    if (data.parimutuel_events[i].game_pot > 0) {
                        var prize_pool = parseFloat(parseInt(data.parimutuel_events[i].game_pot) / 100000000).toFixed(8);
                        var epoch_time = Math.floor((new Date).getTime() / 1000);
                        if (data.parimutuel_events[i].bets_paused > epoch_time) {
                            CountupTimer(".parimutuel_prize_pool_" + data.parimutuel_events[i].game_id, prize_pool, update_interval, 8);
                        } else {
                            $('.parimutuel_prize_pool_' + data.parimutuel_events[i].game_id).html(prize_pool);
                        }
                    }
                    if (data.parimutuel_events[i].time_weight > 0) {
                        $('.parimutuel_time_weight_' + data.parimutuel_events[i].game_id).html('&nbsp;' + data.parimutuel_events[i].time_weight + 'x');
                    }
                    for (var x = 0; x < data.parimutuel_events[i].outcomes.length; x++) {
                        if (data.parimutuel_events[i].outcomes[x].bets_count > 0) {
                            $("#parimutuel_outcome_" + data.parimutuel_events[i].outcomes[x].game_id + "_" + data.parimutuel_events[i].outcomes[x].outcome + "_li .parimutuel_outcome_bets_count").html(commaSeparateNumber(parseInt(data.parimutuel_events[i].outcomes[x].bets_count)));
                        }
                        if (data.parimutuel_events[i].outcomes[x].odds > 0) {
                            $("#parimutuel_outcome_" + data.parimutuel_events[i].outcomes[x].game_id + "_" + data.parimutuel_events[i].outcomes[x].outcome + "_li .parimutuel_outcome_bet_odds").html(data.parimutuel_events[i].outcomes[x].odds);
                        }
                        if (data.parimutuel_events[i].outcomes[x].popularity > 0) {
                            var popularity = parseInt(data.parimutuel_events[i].outcomes[x].popularity);
                            if (popularity > 100) {
                                popularity = 100;
                            }
                            var bar_colour = "red";
                            if (popularity > 24 && popularity < 50) {
                                bar_colour = "orange";
                            } else if (popularity > 49 && popularity < 75) {
                                bar_colour = "yellow";
                            } else if (popularity > 74) {
                                bar_colour = "green";
                            }
                            $("#parimutuel_outcome_" + data.parimutuel_events[i].outcomes[x].game_id + "_" + data.parimutuel_events[i].outcomes[x].outcome + "_li .progress_bar_container_div").html('<p class="' + bar_colour + '_progress_bar" style="width: ' + popularity + '%;"> </p><span style="width: 100%; position: absolute; top:0; left: 0; background-color: transparent; text-align: center; padding-top: 4px; font-weight: 900;">' + popularity + '%</span>');
                        }
                    }
                }
                if (typeof data.daily_jackpot_round !== 'undefined' && data.daily_jackpot_round > 0 && typeof data.daily_jackpot_end !== 'undefined' && data.daily_jackpot_end > epoch_time) {
                    var daily_jp_ranks_table_content = '<h1 style="color:#008235;font-size:20px;text-align: center;margin:0;font-family: \x27Hepta Slab\x27, serif;">DAILY JACKPOT</h1><h3 style="color:#000;text-align: center;font-size:18px;margin:0;">LEADERBOARD</h3><div id="leaderboard_table_header" style="width: 260px; display: block; margin: auto; clear: both; background-image: linear-gradient(to bottom, #ff7c1a, #ff6d00 90%);border-radius: 5px 5px 0 0;height:32px;"><div class="leaderboard_table_header_columns" style="width: 50px;">RANK</div><div class="leaderboard_table_header_columns" style="width: 90px;">USERID</div><div class="leaderboard_table_header_columns" style="width: 120px;">WAGERED</div></div>';
                    for (var i = 0; i < data.daily_jackpot_ranks.length; i++) {
                        data.daily_jackpot_ranks[i].wagered = parseFloat(data.daily_jackpot_ranks[i].wagered / 100000000).toFixed(8);
                        if (data.daily_jackpot_ranks[i].rank == 1) {
                            daily_jp_ranks_table_content = daily_jp_ranks_table_content + '<div id="leaderboard_table_winner_row" style="width: 280px; height: 35px; display: block; margin: auto; clear: both; padding: 11px 0;background-image: linear-gradient(to bottom, #003c5f, #001927 90%);color:gold;box-shadow: 0px 5px 5px #000;"><div style="width:30px;float:left;text-align:center;margin-top:-2px;"><i class="fa fa-trophy" aria-hidden="true"></i></div><div class="leaderboard_table_winner_row_columns" style="width: 30px;font-size:13px;text-align:left;">' + data.daily_jackpot_ranks[i].rank + '</div><div class="leaderboard_table_winner_row_columns" style="width: 90px;font-size:13px;">' + data.daily_jackpot_ranks[i].userid + '</div><div class="leaderboard_table_winner_row_columns" style="width: 120px;font-size:13px;">' + data.daily_jackpot_ranks[i].wagered + '</div></div>';
                        } else {
                            daily_jp_ranks_table_content = daily_jp_ranks_table_content + '<div id="leaderboard_table_header" style="width: 260px; display: block; margin: auto; clear: both;"><div class="leaderboard_table_columns" style="width: 50px;font-size:12px;">' + data.daily_jackpot_ranks[i].rank + '</div><div class="leaderboard_table_columns" style="width: 90px;font-size:12px;">' + data.daily_jackpot_ranks[i].userid + '</div><div class="leaderboard_table_columns" style="width: 120px;font-size:12px;">' + data.daily_jackpot_ranks[i].wagered + '</div></div>';
                        }
                    }
                    daily_jp_ranks_table_content = daily_jp_ranks_table_content + '<div class="daily_jackpot_your_stats_container_div"><div class="daily_jackpot_your_stats_header gold">YOUR STATS</div><div class="daily_jackpot_your_stats_rows"><div class="daily_jackpot_your_stats_rank_wagered_columns">RANK</div><div class="daily_jackpot_your_stats_rank_wagered_values_columns" id="daily_jackpot_user_rank">' + user_daily_jp_rank + '</div></div><div class="daily_jackpot_your_stats_rows" style="display:block; width:100%;"><div class="daily_jackpot_your_stats_rank_wagered_columns" style="border-top: 1px solid transparent;border-radius: 0 0 0 5px;">WAGERED</div><div class="daily_jackpot_your_stats_rank_wagered_values_columns" id="daily_jackpot_user_wagered" style="border-top: 1px solid transparent;border-radius:0 0 5px 0;">' + user_daily_jp_wagered + '</div></div></div>';
                    $("#daily_jackpot_leaderboard_modal_content").html(daily_jp_ranks_table_content);
                    var jp_countdown_iter = update_interval * 60 * 10;
                    var daily_jp_banner_content = '<div class="close_daily_jackpot_main_container_div" onclick="CloseDailyJPBanner();"><i class="fa fa-times-circle" aria-hidden="true"></i></div><div class="black_background_div"><div class="daily_jackpot_prize_div"><div class="background_span"><div class="left_marker"></div><i class="fa fa-btc fa-btc-900"></i><div class="right_marker"></div></div>';
                    daily_jp_banner_content = daily_jp_banner_content + '<div class="background_span"><div class="left_marker"></div><p id="daily_jp_pot_digit_1"></p><div class="right_marker"></div></div><div class="background_span"><div class="left_marker"></div><p class="decimalpoint">.</p><div class="right_marker"></div></div>';
                    for (var i = 2; i < 10; i++) {
                        daily_jp_banner_content = daily_jp_banner_content + '<div class="background_span"><div class="left_marker"></div><p id="daily_jp_pot_digit_' + i + '"></p><div class="right_marker"></div></div>';
                    }
                    daily_jp_banner_content = daily_jp_banner_content + '</div><div class="daily_jackpot_your_rank_div">YOUR RANK&nbsp;<span id="daily_jp_user_rank">#' + user_daily_jp_rank + '</span></div></div>';
                    daily_jp_banner_content = daily_jp_banner_content + '<div class="yellow_background"><h1>WIN DAILY JACKPOT</h1><p>HIGHEST DAILY WAGERER / BETTOR WILL WIN THE JACKPOT!</p></div><div class="daily_jackpot_text_for_extra_small"><p>HIGHEST DAILY WAGERER / BETTOR WILL WIN THE JACKPOT!</p></div><div class="daily_jackpot_text_for_small"><p>HIGHEST DAILY WAGERER / BETTOR WILL WIN THE JACKPOT!</p></div><div class="leaderboard_button_timer_container_div"><div class="leaderboard_winner_button_container_div"><div class="leaderboard_button" data-reveal-id="daily_jackpot_leaderboard_modal"><img src="https://sirv.freebitco.in/1566561624_xUklV3EW.png"><span>LEADERBOARD</span></div><div class="winners_button" data-reveal-id="daily_jackpot_winners_modal"><i class="fa fa-trophy trophy_winners_button" aria-hidden="true"></i><span>WINNERS</span></div></div><div class="timer_div_daily_jackpot"></div></div></div>';
                    $(".daily_jackpot_main_container_div").html(daily_jp_banner_content);
                    if (data.daily_jackpot_end > 0) {
                        $('.timer_div_daily_jackpot').countdown('destroy');
                        $('.timer_div_daily_jackpot').countdown({
                            until: +(data.daily_jackpot_end - epoch_time),
                            format: 'HMS'
                        });
                    }
                    daily_jp_countup_stop = 1;
                    data.daily_jp_starting_pot = data.daily_jackpot_pot - (jp_countdown_iter * 5);
                    if (data.daily_jp_starting_pot < 0) {
                        data.daily_jp_starting_pot = 0;
                    }
                    setTimeout(function() {
                        daily_jp_countup_stop = 0;
                        CountupDailyJPPot(data.daily_jp_starting_pot, data.daily_jackpot_pot, data.daily_jackpot_end);
                    }, 1000);
                }
                if (typeof data.daily_jackpot_winners !== 'undefined') {
                    var daily_jp_winners_table = '<h1 style="color:#008235;font-size:20px;text-align: center;margin:0;font-family: \x27Hepta Slab\x27, serif;">DAILY JACKPOT</h1><h3 style="color:#000;text-align: center;font-size:18px;margin:0;">WINNERS</h3><div id="leaderboard_table_header" style="width: 260px; display: block; margin: auto; clear: both; background-image: linear-gradient(to bottom, #ff7c1a, #ff6d00 90%);border-radius: 5px 5px 0 0;height:32px;"><div class="leaderboard_table_header_columns" style="width: 90px;">DATE</div><div class="leaderboard_table_header_columns" style="width: 70px;">USER ID</div><div class="leaderboard_table_header_columns" style="width: 100px;">PRIZE</div></div>';
                    for (var i = 0; i < data.daily_jackpot_winners.length; i++) {
                        data.daily_jackpot_winners[i].prize = parseFloat(data.daily_jackpot_winners[i].prize / 100000000).toFixed(8);
                        daily_jp_winners_table = daily_jp_winners_table + '<div id="leaderboard_table_header" style="width: 260px; display: block; margin: auto; clear: both;"><div class="leaderboard_table_columns" style="width: 90px;">' + data.daily_jackpot_winners[i].date + '</div><div class="leaderboard_table_columns" style="width: 70px;">' + data.daily_jackpot_winners[i].userid + '</div><div class="leaderboard_table_columns" style="width: 100px;">' + data.daily_jackpot_winners[i].prize + '</div></div>';
                    }
                    $("#daily_jackpot_winners_modal_content").html(daily_jp_winners_table);
                }
            }
        }
    });
    setTimeout(UpdateStats, update_interval * 60 * 1000);
}
function CountupDailyJPPot(starting_pot, final_amount, end_time) {
    if (daily_jp_countup_stop == 0) {
        if (starting_pot < final_amount) {
            var epoch_time = Math.floor((new Date).getTime() / 1000);
            var rand_increment = Math.floor(Math.random() * 10);
            var display_pot = starting_pot + rand_increment;
            if (epoch_time >= end_time) {
                display_pot = final_amount;
            }
            var jp_prize_split = display_pot.toString().split('');
            var length = jp_prize_split.length;
            var length_diff = 9 - jp_prize_split.length;
            if (length_diff > 0) {
                for (var i = 0; i < length_diff; i++) {
                    jp_prize_split.unshift('0');
                }
            }
            for (var i = 1; i < 10; i++) {
                $('#daily_jp_pot_digit_' + i).html(jp_prize_split[i - 1]);
            }
            setTimeout(function() {
                CountupDailyJPPot(display_pot, final_amount, end_time);
            }, 100);
        }
    }
}
function PrintWagerContestTables(parent, info, type) {
    var mobile_class = "";
    if (mobile_device == 1) {
        mobile_class = "lottery_table_mobile_style";
    }
    var wager_contest_prizes = [10000, 5000, 2500, 1250, 1000, 750, 500, 300, 200, 100];
    var ref_contest_prizes = [5000, 2500, 1250, 600, 500, 400, 300, 200, 100, 50];
    var text_to_print = "";
    for (var i = 0; i < info.length; i++) {
        var special_class = "wager_rank_7_8_9_10";
        if (i == 0) {
            special_class = "wager_rank_1";
        } else if (i == 1 || i == 2) {
            special_class = "wager_rank_2_3";
        } else if (i == 3 || i == 4 || i == 5) {
            special_class = "wager_rank_4_5_6";
        }
        var prize = wager_contest_prizes[i];
        var wagered = parseFloat(info[i].wagered / 100000000).toFixed(8);
        if (type == "ref") {
            prize = ref_contest_prizes[i];
        }
        var rank = i + 1;
        text_to_print = text_to_print + '<div class="large-12 small-12 columns center ' + special_class + '_container"><div class="large-1 small-1 columns center wager_table_cell ' + special_class + ' lottery_winner_table_first_last_cell ' + mobile_class + '"><p class="wager_rank">' + rank + '</p></div><div class="large-11 small-11 columns"><div class="row"><div class="large-3 small-3 columns center wager_table_cell ' + special_class + ' lottery_winner_table_second_cell ' + mobile_class + '">' + info[i].userid + '</div> <div class="large-5 small-5 columns center wager_table_cell ' + special_class + ' lottery_winner_table_second_cell ' + mobile_class + '"><i class="fa fa-btc" aria-hidden="true"></i>&nbsp;<span>' + ReplaceNumberWithCommas(wagered) + '</span></div><div class="large-4 small-4 columns center wager_table_cell ' + special_class + ' lottery_winner_table_third_cell ' + mobile_class + '" style="border-right: none;">$&nbsp;<span>' + ReplaceNumberWithCommas(prize) + '</span></div></div></div></div>';
    }
    $("#" + parent).html(text_to_print);
}
function InitialUserStats() {
    $.get('/stats_new_private/?u=' + socket_userid + '&p=' + socket_password + '&f=user_stats_initial', function(data) {
        if (data.status == "success") {
            GenerateStatsTables('PAYMENTS SENT (LAST 30 DAYS, MAX. 25)', 'TIME', 'ADDRESS', 'AMOUNT', 'TRANSACTION', data.payments_sent, 'personal_stats_page_tables');
            GenerateStatsTables('DEPOSITS (LAST 25)', 'TIME', 'ADDRESS', 'AMOUNT', 'TRANSACTION', data.deposits, 'personal_stats_page_tables');
            for (property in data.user) {
                if (property == "free_spins_played" || property == "paid_spins_played") {
                    $("#user_" + property).html((ReplaceNumberWithCommas(parseInt(data.user[property]))));
                } else {
                    $("#user_" + property).html((ReplaceNumberWithCommas(parseFloat(data.user[property] / 100000000).toFixed(8))));
                }
            }
            if (data.lambo_lottery_ends > 0) {
                $('.golden_ticket_time_remaining').countdown({
                    until: +data.lambo_lottery_ends,
                    format: 'DHMS'
                });
            }
            if (data.user.free_spins_played > 0) {
                new_user_first_load = 0;
                $('#free_play_alert_boxes').show();
                $('#fp_multiplier_bonuses_main_div').show();
                $('#fp_provably_fair_link').show();
                $('#play_without_captcha_container').show();
                InsertAlertMsg('lambo_contest', 'Play <a href="javascript:void(0);" onclick="SwitchPageTabs(\x27double_your_btc\x27);">MULTIPLY BTC</a> or <a href="javascript:void(0);" onclick="SwitchPageTabs(\x27betting\x27);">BET ON EVENTS</a> and you could win a <a href="javascript:void(0);" onclick="SwitchPageTabs(\x27golden_ticket\x27);">Lamborghini Huracan</a>!', 7);
            } else {
                new_user_first_load = 1;
                $('#new_user_win_msg').show();
                $('#req_for_bonuses_link').hide();
                $("#deposit_promo_message_mobile").hide();
                $("#deposit_promo_message_regular").hide();
                $('#fp_multiplier_bonuses_main_div .fp_multiplier_bonus_box').html("<p style='font-size: 20px;''><span class='free_play_bonus_box_span_large'>WELCOME TO FREEBITCO.IN</span></p><p>We're happy to have you here. Claim your Free BTC on this page or check out the website to begin the most rewarding crypto experience of your life!</p>");
                $('#fp_multiplier_bonuses_main_div').show();
            }
            user_stats_loaded = 1;
        }
    });
}
function InitialStatsLoad() {
    $.get('/cf_stats_public/?f=public_stats_initial', function(data) {
        if (typeof data != 'undefined') {
            if (data.status == "success") {
                google.setOnLoadCallback(drawChart("btc_won", "BTC"));
                google.setOnLoadCallback(drawChart("wagered", "BTC"));
                google.setOnLoadCallback(drawChart("total_plays", "Plays"));
                google.setOnLoadCallback(drawChart("total_signups", "Signups"));
                google.setOnLoadCallback(drawChart("referral_commissions", "BTC"));
                google.setOnLoadCallback(drawChart("total_user_savings", "BTC"));
                google.setOnLoadCallback(drawChart("total_interest_paid", "BTC"));
                function drawChart(variable, col_name) {
                    if ($('#' + variable + '_div').length) {
                        var chart_height = 300;
                        var chart_width = 600;
                        if (mobile_device == 1) {
                            chart_height = 250;
                            chart_width = 300;
                        }
                        var chart_arr = [['Date', col_name]];
                        for (var i = 0; i < data[variable].length; i++) {
                            chart_arr.push([data[variable][i].date, parseFloat(data[variable][i][variable])]);
                        }
                        var data2 = google.visualization.arrayToDataTable(chart_arr);
                        var options = {
                            pointSize: 8,
                            width: chart_width,
                            height: chart_height
                        };
                        var chart = new google.visualization.AreaChart(document.getElementById(variable + '_div'));
                        chart.draw(data2, options);
                    }
                }
                if (rp_rewards_list_loaded == 0) {
                    var mobile_class_one = "";
                    var mobile_class_two = "";
                    if (mobile_device == 1) {
                        mobile_class_one = " reward_link_redeem_button_mobile ";
                        mobile_class_two = " reward_link_redeem_button_mobile_last ";
                    }
                    for (var i = 0; i < data.rp_prizes.length; i++) {
                        $("#" + data.rp_prizes[i].category + "_rewards").append('<div class="effect2" style="margin: 0; border-radius: 3px; margin-top: 20px;"><div class="row reward_product_name">' + data.rp_prizes[i].product_name + '</div><div class="row" style="margin:0; padding: 10px 0; border: 1px solid #bdbcb8; border-radius: 0 0 3px 3px; background:#fff;"><div class="large-3 small-12 columns"><div class="reward_link_redeem_button_style' + mobile_class_one + '" onclick="VisitLink(\x27' + data.rp_prizes[i].product_link + '\x27)">LINK</div></div><div class="large-6 small-12 columns"><div class="reward_dollar_value_style' + mobile_class_one + '">' + data.rp_prizes[i].points + ' RP</div></div><div class="large-3 small-12 columns"><button class="reward_link_redeem_button_style ' + mobile_class_two + '" onclick="RedeemRPProduct(\x27' + data.rp_prizes[i].product_type + '\x27)">REDEEM</button></div></div></div>');
                    }
                    rp_rewards_list_loaded = 1;
                }
                GenerateStatsTables('TOP 10 JACKPOT WINNERS', 'ADDRESS', 'JACKPOT AMOUNT', 'FREE ROLLS', 'MULTIPLY ROLLS', data.jackpot_winners, 'stats_page_tables');
                GenerateStatsTables('TOP 10 OVERALL WINNERS', 'ADDRESS', 'TOTAL WON', 'FREE ROLLS', 'MULTIPLY ROLLS', data.top_10_winners, 'stats_page_tables');
                GenerateStatsTables('TOP 10 AFFILIATES', 'ADDRESS', 'COMMISSIONS', 'REFERRED', 'SHARED', data.top_referrers, 'stats_page_tables');
                GenerateStatsTables('LAST 10 PAYMENTS SENT', 'DATE', 'TYPE', 'AMOUNT', 'TX', data.sent_payments, 'stats_page_tables');
            }
        }
    });
}
function GenerateStatsTables(title, cname1, cname2, cname3, cname4, data_obj, pop_div) {
    var content = '<div class="row" style="margin-top:20px;margin-bottom: 20px;"><div class="large-9 small-12 large-centered small-centered columns"><div class="large-12 small-12 columns center lottery_winner_table_box table_header_background br_5_5" style="padding: 15px 0;">' + title + '</div>';
    if (mobile_device == 1) {
        for (var i = 0; i < data_obj.length; i++) {
            content = content + '<div class="new_stats_table_for_small"><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2 multiply_history_table_header"><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_first_last_cell font_bold">' + cname1 + '</div><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">' + cname2 + '</div><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_first_last_cell">' + data_obj[i].c1 + '</div> <div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + data_obj[i].c2 + '</div></div><div style="margin-bottom: 10px;"><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_third_cell font_bold" style="border-left: 1px solid #ccc;">' + cname3 + '</div><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_first_last_cell font_bold">' + cname4 + '</div><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_third_cell" style="border-left: 1px solid #ccc;">' + data_obj[i].c3 + '</div><div class="large-3 small-6 columns center lottery_winner_table_box lottery_winner_table_first_last_cell">' + data_obj[i].c4 + '</div></div></div><div class="large-12 small-12 columns center" style="height:5px;"></div></div>';
        }
    } else {
        content = content + '<div class="new_stats_table_for_big"><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2 multiply_history_table_header"><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_first_last_cell font_bold">' + cname1 + '</div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell font_bold">' + cname2 + '</div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_third_cell font_bold"><span>' + cname3 + '</span></div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_first_last_cell font_bold"><span>' + cname4 + '</span></div></div>';
        for (var i = 0; i < data_obj.length; i++) {
            content = content + '<div><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_first_last_cell">' + data_obj[i].c1 + '</div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell">' + data_obj[i].c2 + '</div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_third_cell">' + data_obj[i].c3 + '</div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_first_last_cell">' + data_obj[i].c4 + '</div></div></div>';
        }
        content = content + '</div>';
    }
    content = content + '</div></div>';
    $("#" + pop_div).append(content);
}
function InsertAlertMsg(type, content, expires) {
    var expiry = 3650;
    if (expires > 0) {
        expiry = expires;
    }
    if ($.cookie("hide_" + type + "_msg") != 1) {
        var to_pub = '<div class="alert-box" id="' + type + '_msg" style="background-color:#FFFFAD;color:black;" align=center>' + content + '<a href="javascript:void(0);" class="close" onclick="CloseAlertMsg(\x27' + type + '\x27,' + expiry + ');">&times;</a></div>';
        $("#changing_rewards_link").after(to_pub);
    }
}
function PreviousContestWinners(round_number) {
    if (round_number > 0) {
        $.get('/stats_new_public/?f=wager_contest_winners&cbreak=4&round=' + round_number, function(data) {
            if (data.status == "success") {
                $(".prev_contest_round_title").html('CONTEST ROUND ' + round_number + ' WINNERS');
                var mobile_class = "";
                if (mobile_device == 1) {
                    mobile_class = "lottery_table_mobile_style";
                }
                $("#contest_winner_table_user_list").html('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="font_bold large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell gold ' + mobile_class + '"> # </div><div class="font_bold large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell gold ' + mobile_class + '"> USERID </div><div class="font_bold large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell gold ' + mobile_class + '"> AMOUNT WON </div><div class="font_bold large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell gold ' + mobile_class + '"> WAGERED </div></div>');
                $("#contest_winner_table_referrer_list").html('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="font_bold large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell gold ' + mobile_class + '"> # </div><div class="font_bold large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell gold ' + mobile_class + '"> USERID </div><div class="font_bold large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell gold ' + mobile_class + '"> AMOUNT WON </div><div class="font_bold large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell gold ' + mobile_class + '"> REF. WAGERED </div></div>');
                for (var x = 1; x < 11; x++) {
                    for (var i = 0; i < data.contest_winners.length; i++) {
                        if (parseInt(data.contest_winners[i].rank) == x) {
                            if (data.contest_winners[i].contest_type == "wager") {
                                var prize = parseFloat(Math.round((data.contest_winners[i].prize / 100000000) * 100000000) / 100000000).toFixed(8);
                                var wagered = parseFloat(Math.round((data.contest_winners[i].wagered / 100000000) * 100000000) / 100000000).toFixed(8);
                                $("#contest_winner_table_user_list").append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> ' + data.contest_winners[i].rank + ' </div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell ' + mobile_class + '"> ' + data.contest_winners[i].userid + ' </div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell ' + mobile_class + '"> <i class="fa fa-btc" aria-hidden="true"></i> ' + prize + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> <i class="fa fa-btc" aria-hidden="true"></i> ' + wagered + ' </div></div>')
                            } else if (data.contest_winners[i].contest_type == "ref") {
                                var prize = parseFloat(Math.round((data.contest_winners[i].prize / 100000000) * 100000000) / 100000000).toFixed(8);
                                var wagered = parseFloat(Math.round((data.contest_winners[i].wagered / 100000000) * 100000000) / 100000000).toFixed(8);
                                $("#contest_winner_table_referrer_list").append('<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-1 small-1 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> ' + data.contest_winners[i].rank + ' </div><div class="large-3 small-3 columns center lottery_winner_table_box lottery_winner_table_second_cell ' + mobile_class + '"> ' + data.contest_winners[i].userid + ' </div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell ' + mobile_class + '"> <i class="fa fa-btc" aria-hidden="true"></i> ' + prize + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> <i class="fa fa-btc" aria-hidden="true"></i> ' + wagered + ' </div></div>');
                            }
                        }
                    }
                }
            }
        });
    }
}
function CloseAlertMsg(type, expires) {
    var expiry = 3650;
    if (expires > 0) {
        expiry = expires;
    }
    $("#" + type + "_msg").hide();
    $.cookie.raw = true;
    $.cookie("hide_" + type + "_msg", 1, {
        expires: expiry,
        secure: true
    });
}
function RenewCookies() {
    var cookie_names = ['mobile', 'login_auth', 'referrer', 'tag', 'btc_address', 'password', 'have_account', 'free_play_sound', 'hide_earn_btc_msg', 'hide_mine_btc_msg', 'default_captcha', 'userid'];
    $.cookie.raw = true;
    for (var i = 0; i < cookie_names.length; i++) {
        var cookie_value = $.cookie(cookie_names[i]);
        if (cookie_value != null) {
            $.cookie(cookie_names[i], cookie_value, {
                expires: 3650,
                secure: true
            });
        }
    }
}
function changeContainerDiv_parimutuel() {
    if ($('body').innerWidth() < 990 && $('body').innerWidth() > 767) {
        $("#main_content").addClass('large-12');
        $("#main_content").removeClass('large-9');
    } else {
        $("#main_content").addClass('large-9');
        $("#main_content").removeClass('large-12');
    }
}
function changeContainerDiv_others_parimutuel() {
    $("#main_content").addClass('large-9');
    $("#main_content").removeClass('large-12');
}
function change_box_size_parimutuel() {
    if ($('body').innerWidth() < 990 && $('body').innerWidth() > 767) {
        $(".timer_span_for_768_up").addClass('timer_span_width_for_768_up');
        $(".timer_div_for_768_up").addClass('timer_div_width_for_768_up');
        $(".countdown_row").addClass('countdown_row_for_768_up');
    }
    if ($('body').innerWidth() > 989) {
        $(".timer_span_for_768_up").removeClass('timer_span_width_for_768_up');
        $(".timer_div_for_768_up").removeClass('timer_div_width_for_768_up');
        $(".countdown_row").removeClass('countdown_row_for_768_up');
    }
    if ($('body').innerWidth() < 768) {
        $(".timer_span_for_768_up").removeClass('timer_span_width_for_768_up');
        $(".timer_div_for_768_up").removeClass('timer_div_width_for_768_up');
        $(".countdown_row").removeClass('countdown_row_for_768_up');
        $(".bets_change_timer_container_size").addClass('large-12');
        $(".bets_change_timer_container_size").removeClass('large-7');
    }
    if ($('body').innerWidth() < 1041 && $('body').innerWidth() > 767) {
        $(".change_size_medium_left").addClass('large-12');
        $(".change_size_medium_left").removeClass('large-5');
        $(".change_size_medium_right").addClass('large-12');
        $(".change_size_medium_right").removeClass('large-7');
    }
    if ($('body').innerWidth() > 1041) {
        $(".change_size_medium_left").addClass('large-5');
        $(".change_size_medium_left").removeClass('large-12');
        $(".change_size_medium_right").addClass('large-7');
        $(".change_size_medium_right").removeClass('large-12');
        $(".change_size_medium_left").addClass('reward_table_box_left');
        $(".change_size_medium_left").removeClass('reward_table_box_left_mobile');
        $(".change_size_medium_right").addClass('reward_table_box_right');
        $(".change_size_medium_right").removeClass('reward_table_box_right_mobile');
    }
    if ($('body').innerWidth() < 1041) {
        $(".change_size_medium_left").addClass('reward_table_box_left_mobile');
        $(".change_size_medium_left").removeClass('reward_table_box_left');
        $(".change_size_medium_right").addClass('reward_table_box_right_mobile');
        $(".change_size_medium_right").removeClass('reward_table_box_right');
    }
}
function ParimutuelPlaceBet(game_id, outcome) {
    var bet_amount = $("#parimutuel_outcome_" + game_id + "_" + outcome + "_li .parimutuel_bet_amount").val();
    $.get('/cgi-bin/api.pl?op=parimutuel_bet&game_id=' + game_id + "&outcome=" + outcome + "&bet_amount=" + bet_amount, function(data) {
        DisplaySEMessage(data.status, data.msg);
        if (data.status == "s") {
            $('#balance').html(data.balance);
            balanceChanged();
            var outcome_name = data.outcome_name;
            var bet_amount = parseFloat(data.bet_amount).toFixed(8);
            var mobile_class = "";
            if ($('body').innerWidth() < 768) {
                mobile_class = " lottery_table_mobile_style ";
            }
            var to_append = '<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + outcome_name + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell' + mobile_class + '">' + bet_amount + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '"><font color=red>0.00000000</font></div> </div>';
            $("#parimutuel_game_container_page .parimutuel_bet_history_table").prepend(to_append);
        }
    });
}
function OpenParimutuelGame(game_id) {
    if (parimutuel_all_events_json.status == "success") {
        for (var x = 0; x < parimutuel_all_events_json.details.length; x++) {
            if (parimutuel_all_events_json.details[x].game_id == game_id) {
                $("#parimutuel_main_page_div").hide();
                $("#parimutuel_page_main_text").hide();
                $("#parimutuel_game_container_page").show();
                $("#parimutuel_back_to_all_events_button_div").show();
                var prize_pool = parseFloat(parseInt(parimutuel_all_events_json.details[x].game_pot) / 100000000).toFixed(8);
                var epoch_time = Math.floor((new Date).getTime() / 1000);
                var time_remaining_parimutuel = parseInt(parimutuel_all_events_json.details[x].bets_paused) - epoch_time;
                var ending_header = "ENDING IN";
                var countdown_timer = '<div class="hasCountdown" style="margin: auto; width: 240px !important; padding: 11px 0; background-color: transparent !important; border: none !important;"><div class="countdown_row countdown_show4 time_remaining_' + parimutuel_all_events_json.details[x].game_id + '" style="height: 30px !important; border: 0 !important; background: transparent !important;"></div></div>';
                var tw_box = '<div class="large-5 large-centered small-12 small-centered columns" style="margin: 20px auto;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: center; padding-left: 10px;">TIME WEIGHT MULTIPLIER<a class="auto_bet_setting_span" style="float: right; color: black; border: 1px solid #000;padding: 5px;" href="javascript:void(0);" data-reveal-id="time_weight_modal">?</a></div><div class="reward_table_box br_0_0_5_5" style="border-top:none; padding: 15px;"><span style="font-size: 25px; font-weight: 900; color: #000; text-shadow: 0px 0px 5px #fff;" class="parimutuel_time_weight_' + parimutuel_all_events_json.details[x].game_id + '">&nbsp;' + parimutuel_all_events_json.details[x].time_weight + 'x</span> </div></div>';
                if (parimutuel_all_events_json.details[x].paid_out > 0) {
                    var outcome_name;
                    for (var i = 0; i < parimutuel_all_events_json.details[x].outcomes.length; i++) {
                        if (parimutuel_all_events_json.details[x].outcomes[i].outcome == parimutuel_all_events_json.details[x].winner) {
                            outcome_name = parimutuel_all_events_json.details[x].outcomes[i].name;
                        }
                    }
                    ending_header = "WINNER";
                    countdown_timer = '<div class="hasCountdown" style="margin: auto; width: 240px !important; padding: 11px 0; background-color: transparent !important; border: none !important;"><span style="font-size: 25px; font-weight: 900; color: blue; text-shadow: 0px 0px 5px #fff;">' + outcome_name + '</span></div>';
                }
                if (time_remaining_parimutuel < 1) {
                    tw_box = '';
                }
                var to_append = '<div class="row" style="margin:0; padding:0;"><div class="row"><div class="large-12 large-centered columns"><h3 style="width: 320px; margin-right: auto; margin-left: auto; text-align: center;text-transform: uppercase;"><u>' + parimutuel_all_events_json.details[x].name + '</u></h3><p>' + parimutuel_all_events_json.details[x].game_summary + '</p></div></div><div class="large-5 large-centered small-12 small-centered columns" style="margin-top: 20px;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: center; padding-left: 10px;">' + ending_header + '</div><div class="reward_table_box br_0_0_5_5" style="border-top:none; padding: 10px;">' + countdown_timer + '</div></div>' + tw_box + '<div class="large-5 large-centered small-12 small-centered columns" style="margin: 20px auto;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: center; padding-left: 10px;">PRIZE POOL</div><div class="reward_table_box br_0_0_5_5" style="border-top:none; padding: 15px;"><i class="fa fa-btc" style="font-size: 25px; font-weight: 900; color: #008600; text-shadow: 0px 0px 5px #fff;"></i>&nbsp;<span style="font-size: 25px; font-weight: 900; color: #008600; text-shadow: 0px 0px 5px #fff;" class="parimutuel_prize_pool_' + parimutuel_all_events_json.details[x].game_id + '">' + prize_pool + '</span></div></div><div class="large-12 large-centered columns betting_container_for_768_up"><ul class="small-block-grid-1 medium-block-grid-3 large-block-grid-3 center">';
                for (var i = 0; i < parimutuel_all_events_json.details[x].outcomes.length; i++) {
                    var popularity = parseInt(parimutuel_all_events_json.details[x].outcomes[i].popularity);
                    if (popularity > 100) {
                        popularity = 100;
                    }
                    var bar_colour = "red";
                    if (popularity > 24 && popularity < 50) {
                        bar_colour = "orange";
                    } else if (popularity > 49 && popularity < 75) {
                        bar_colour = "yellow";
                    } else if (popularity > 74) {
                        bar_colour = "green";
                    }
                    to_append = to_append + '<li style="padding: 10px;" id="parimutuel_outcome_' + parimutuel_all_events_json.details[x].game_id + '_' + parimutuel_all_events_json.details[x].outcomes[i].outcome + '_li"><div style="height: auto; border: 2px solid white; box-shadow: 0 0 5px black; padding-bottom:20px; background: transparent;"><div class="center reward_table_box table_header_background" style="padding: 10px; font-size: 15px; font-weight: 900;">' + parimutuel_all_events_json.details[x].outcomes[i].name + '</div><div class="large-12 large-centered small-12 small-centered columns" style="margin: 20px 0;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: center; padding: 5px;">BETS COUNT</div><div class="reward_table_box br_0_0_5_5 parimutuel_outcome_bets_count" style="border-top:none; padding: 10px;">' + commaSeparateNumber(parseInt(parimutuel_all_events_json.details[x].outcomes[i].bets_count)) + '</div></div><div class="large-12 large-centered small-12 small-centered columns" style="margin: 20px 0;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: center; padding: 5px;">OUTCOME ODDS<a class="auto_bet_setting_span" style="float: right; margin-top: 3px; color: black; border: 1px solid #000;">?<span style="right: 15px; max-width: 280px; font-weight: normal;">Estimated odds for this outcome. Actual odds will be calculated after betting ends for this event.</span></a></div><div class="reward_table_box br_0_0_5_5 parimutuel_outcome_bet_odds" style="border-top:none; padding: 10px;">' + parimutuel_all_events_json.details[x].outcomes[i].odds + '</div></div><div class="large-10 large-centered small-12 small-centered progress_bar_container_div"><p class="' + bar_colour + '_progress_bar" style="width: ' + popularity + '%;"> </p><span style="width: 100%; position: absolute; top:0; left: 0; background-color: transparent; text-align: center; padding-top: 4px; font-weight: 900;">' + popularity + '%</span></div><div class="large-12 small-12 columns center reward_table_box reward_table_box_container" style="margin: 9px 0; border:none; background: transparent;" ><div class="large-12 small-12 columns center reward_table_box reward_table_box_left_mobile bronze" style="border: 1px solid #d87310;padding: 7px 0;">BET AMOUNT</div><div class="row" style="margin:0; padding:0;"><div class="large-12 small-12 columns center reward_table_input reward_table_box_right_mobile" style="background: transparent;"><div class="large-2 small-4 columns" style="background: #f2f2f2; height: 2.3125em; padding: 4px; border: 1px solid #ccc; border-right: none;"><i class="fa fa-btc" style="text-align: center;"></i></div><div class="large-10 small-8 columns" style="padding: 0;"><input type="text" style="text-align: center; color: #000; border-radius:0 0 3px 3px;" class="parimutuel_bet_amount" placeholder="Enter Bet Amount" onfocus="javascript:ParimutuelFocus(\x27' + parimutuel_all_events_json.details[x].game_id + '\x27,\x27' + parimutuel_all_events_json.details[x].outcomes[i].outcome + '\x27);" onkeypress="javascript:ParimutuelFocus(\x27' + parimutuel_all_events_json.details[x].game_id + '\x27,\x27' + parimutuel_all_events_json.details[x].outcomes[i].outcome + '\x27);" onkeydown="javascript:ParimutuelFocus(\x27' + parimutuel_all_events_json.details[x].game_id + '\x27,\x27' + parimutuel_all_events_json.details[x].outcomes[i].outcome + '\x27);" onkeyup="javascript:ParimutuelFocus(\x27' + parimutuel_all_events_json.details[x].game_id + '\x27,\x27' + parimutuel_all_events_json.details[x].outcomes[i].outcome + '\x27);"></div> </div></div><div class="row parimutuel_estimated_winnings" style="margin-top: 1px; padding:0;"><div class="large-12 small-12 columns large-centered small-centered center" style="width:auto;"><p class="odd_win_chance_message" style="display: block;">Estimated winnings:&nbsp;<span style="overflow: hidden;white-space: nowrap;"><i class="fa fa-btc"></i>0.00000000</span></p></div></div></div><button class="auto_bet_start_stop_button" id="got_play_now" style="padding:10px;" onclick="javascript:ParimutuelPlaceBet(\x27' + parimutuel_all_events_json.details[x].game_id + '\x27,\x27' + parimutuel_all_events_json.details[x].outcomes[i].outcome + '\x27);">&nbsp;&nbsp;&nbsp;BET&nbsp;&nbsp;&nbsp;</button></div></li> ';
                }
                to_append = to_append + '</ul> </div></div>';
                to_append = to_append + '<div class="large-7 large-centered small-12 small-centered columns" style="margin-top: 20px;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: left; padding-left: 10px;">DECISION LOGIC</div><div class="reward_table_box br_0_0_5_5 font_bold" style="border-top:none; padding: 10px;">';
                var split_game_logic = parimutuel_all_events_json.details[x].game_logic.split("|");
                for (var i = 0; i < split_game_logic.length; i++) {
                    to_append = to_append + '<p style="margin:0; text-transform: none; text-align: left;">' + split_game_logic[i] + '</p>';
                }
                to_append = to_append + '</div></div>';
                $("#parimutuel_game_container_page").html(to_append);
                $('.time_remaining_' + parimutuel_all_events_json.details[x].game_id).countdown({
                    until: +time_remaining_parimutuel,
                    format: 'DHMS'
                });
                change_box_size_parimutuel();
                $("html, body").animate({
                    scrollTop: 0
                }, "fast");
                if (parimutuel_bet_history_json.status == "success") {
                    var mobile_class = "";
                    if ($('body').innerWidth() < 768) {
                        mobile_class = " lottery_table_mobile_style ";
                    }
                    var to_append = '<div class="row" style="margin-top:20px;margin-bottom: 20px;"><div class="large-7 small-12 large-centered small-centered columns change_size_css"><div class="large-12 small-12 columns center lottery_winner_table_box table_header_background br_5_5"><div class="center" style="margin:auto;">YOUR BETS ON THIS EVENT</div></div><div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> <span class="bold">OUTCOME</span> </div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell ' + mobile_class + '"> <span class="bold">BET</span> </div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell ' + mobile_class + '"> <span class="bold">WINNINGS</span> </div></div><div class="parimutuel_bet_history_table">';
                    for (var i = 0; i < parimutuel_bet_history_json.bets.length; i++) {
                        if (parimutuel_bet_history_json.bets[i].game_id == game_id) {
                            var outcome_name;
                            for (var m = 0; m < parimutuel_all_events_json.details[x].outcomes.length; m++) {
                                if (parimutuel_all_events_json.details[x].outcomes[m].outcome == parimutuel_bet_history_json.bets[i].outcome) {
                                    outcome_name = parimutuel_all_events_json.details[x].outcomes[m].name;
                                }
                            }
                            var bet_amount = parseFloat(parimutuel_bet_history_json.bets[i].bet_amount / 100000000).toFixed(8);
                            var winnings = parseFloat(parimutuel_bet_history_json.bets[i].winnings / 100000000).toFixed(8);
                            if (winnings < 0.00000001) {
                                winnings = '<font color=red>' + winnings + '</font>';
                            } else {
                                winnings = '<font color=green>' + winnings + '</font>';
                            }
                            to_append = to_append + '<div class="large-12 small-12 columns center lottery_winner_table_box_container effect2"><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + outcome_name + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_third_cell' + mobile_class + '">' + bet_amount + '</div><div class="large-4 small-4 columns center lottery_winner_table_box lottery_winner_table_first_last_cell' + mobile_class + '">' + winnings + '</div> </div>';
                        }
                    }
                    to_append = to_append + '</div></div></div><p>Your bets will appear here within 1 hour of being placed.</p>';
                    $("#parimutuel_game_container_page").append(to_append);
                }
            }
        }
    }
}
function ParimutuelFocus(game_id, outcome) {
    var bet_amount = parseFloat($("#parimutuel_outcome_" + game_id + "_" + outcome + "_li .parimutuel_bet_amount").val());
    var odds = parseFloat($("#parimutuel_outcome_" + game_id + "_" + outcome + "_li .parimutuel_outcome_bet_odds").html());
    var potential_winnings = parseFloat(bet_amount * odds * 100000000 / 100000000).toFixed(8);
    if (isNaN(potential_winnings) == true) {
        potential_winnings = '0.00000000';
    }
    $("#parimutuel_outcome_" + game_id + "_" + outcome + "_li .parimutuel_estimated_winnings").html('<div class="large-12 small-12 columns large-centered small-centered center" style="width:auto;"><p class="odd_win_chance_message" style="display: block;">Estimated winnings:&nbsp;<span style="overflow: hidden;white-space: nowrap;"><i class="fa fa-btc"></i>' + potential_winnings + '</span></p></div>');
}
function LoadParimutuelEvents(category) {
    $("#please_wait_loading_page").hide();
    $("#parimutuel_main_page_ul").html('');
    var parimutuel_all_events_json_temp = JSON.parse(JSON.stringify(parimutuel_all_events_json));
    if (category == "expired" || category == "my_expired_bets" || category == "ending_soon") {
        parimutuel_all_events_json_temp.details.sort(function(a, b) {
            return a.bets_paused.toString().localeCompare(b.bets_paused.toString());
        });
        if (category == "expired" || category == "my_expired_bets") {
            parimutuel_all_events_json_temp.details.reverse();
        }
    } else if (category == "new") {
        parimutuel_all_events_json_temp.details.sort(function(a, b) {
            return a.starts.toString().localeCompare(b.starts.toString());
        });
        parimutuel_all_events_json_temp.details.reverse();
    } else if (category == "popular") {
        parimutuel_all_events_json_temp.details.sort(function(a, b) {
            return a.game_pot.toString().localeCompare(b.game_pot.toString());
        });
        parimutuel_all_events_json_temp.details.reverse();
    } else {
        parimutuel_all_events_json_temp = parimutuel_all_events_json;
    }
    var new_bets_counter = 0;
    var popular_bets_counter = 0;
    var ending_soon_counter = 0;
    for (var i = 0; i < parimutuel_all_events_json_temp.details.length; i++) {
        var prize_pool = parseFloat(parseInt(parimutuel_all_events_json_temp.details[i].game_pot) / 100000000).toFixed(8);
        var epoch_time = Math.floor((new Date).getTime() / 1000);
        var time_remaining_parimutuel = parseInt(parimutuel_all_events_json_temp.details[i].bets_paused) - epoch_time;
        var time_running_parimutuel = epoch_time - parseInt(parimutuel_all_events_json_temp.details[i].starts);
        var timer_text = '<div class="countdown_row countdown_show4 time_remaining_' + parimutuel_all_events_json_temp.details[i].game_id + '" style="height:61px;"></div>';
        var display_event = 0;
        if (category == "main") {
            if (time_remaining_parimutuel > 0) {
                display_event = 1;
            }
        } else {
            if (category == parimutuel_all_events_json_temp.details[i].category && time_remaining_parimutuel > 0) {
                display_event = 1;
            }
            if (category == "popular" && time_remaining_parimutuel > 0) {
                if (prize_pool > 0.5 || popular_bets_counter < 12) {
                    display_event = 1;
                    popular_bets_counter = popular_bets_counter + 1;
                }
            }
            if (category == "ending_soon" && time_running_parimutuel > 0 && time_remaining_parimutuel > 0) {
                if (time_remaining_parimutuel < 24 * 60 * 60 || ending_soon_counter < 12) {
                    display_event = 1;
                    ending_soon_counter = ending_soon_counter + 1;
                }
            }
            if (category == "new" && time_running_parimutuel > 0 && time_remaining_parimutuel > 0) {
                if (time_running_parimutuel < 24 * 60 * 60 || new_bets_counter < 12) {
                    display_event = 1;
                    new_bets_counter = new_bets_counter + 1;
                }
            }
            if (category == "my_bets" && time_remaining_parimutuel > 0) {
                for (var x = 0; x < parimutuel_bet_history_json.bets.length; x++) {
                    if (parimutuel_bet_history_json.bets[x].game_id == parimutuel_all_events_json_temp.details[i].game_id) {
                        display_event = 1;
                    }
                }
            }
            if (time_remaining_parimutuel < 0) {
                if (category == "pending" && parimutuel_all_events_json_temp.details[i].paid_out < 1) {
                    display_event = 1;
                    timer_text = '<div class="countdown_row countdown_show4"><span style="font-size: 25px; font-weight: 900; color: red; text-shadow: 0px 0px 5px #fff;">PENDING</span></div>';
                } else if (category == "expired" && parimutuel_all_events_json_temp.details[i].paid_out > 0) {
                    display_event = 1;
                    timer_text = '<div class="countdown_row countdown_show4"><span style="font-size: 25px; font-weight: 900; color: blue; text-shadow: 0px 0px 5px #fff;">PAID OUT</span></div>';
                } else if (category == "my_expired_bets") {
                    for (var x = 0; x < parimutuel_bet_history_json.bets.length; x++) {
                        if (parimutuel_bet_history_json.bets[x].game_id == parimutuel_all_events_json_temp.details[i].game_id) {
                            display_event = 1;
                            if (parimutuel_all_events_json_temp.details[i].paid_out < 1) {
                                timer_text = '<div class="countdown_row countdown_show4"><span style="font-size: 25px; font-weight: 900; color: red; text-shadow: 0px 0px 5px #fff;">PENDING</span></div>';
                            } else {
                                timer_text = '<div class="countdown_row countdown_show4"><span style="font-size: 25px; font-weight: 900; color: blue; text-shadow: 0px 0px 5px #fff;">PAID OUT</span></div>';
                            }
                        }
                    }
                }
            }
        }
        if (display_event == 1) {
            $("#parimutuel_main_page_ul").append('<li style="padding: 10px;"><div style="height: auto; border: 2px solid white; box-shadow: 0 0 5px black; padding-bottom:20px; background: transparent;"><div class="center reward_table_box table_header_background" style="padding: 10px; font-size: 15px; font-weight: 900;text-transform: uppercase;">' + parimutuel_all_events_json_temp.details[i].name + '</div><div style="background-image: url(' + parimutuel_all_events_json_temp.details[i].bg_image + ');background-repeat: no-repeat;background-size: cover;"><img src="' + parimutuel_all_events_json_temp.details[i].fg_image + '"></img></div><p style="color: black; font-weight: 500; border-bottom: 1px solid #ccc; margin: 0; padding: 4px; text-align: left; font-size: 15px;">' + parimutuel_all_events_json_temp.details[i].game_summary + '</p><div class="large-12 large-centered small-12 small-centered columns" style="margin: 20px 0;"><div class="reward_table_box gold br_5_5 bold" style="border-bottom: 1px solid #f3cd00; font-weight: bold; text-align: center; padding: 9px;">PRIZE POOL</div><div class="reward_table_box br_0_0_5_5" style="border-top:none; padding: 10px; background: transparent !important;"><i class="fa fa-btc" style="font-size: 25px; font-weight: 900; color: #008600;"></i>&nbsp;<span style="font-size: 25px; font-weight: 900; color: #008600; text-shadow: 0px 0px 5px #fff;" class="parimutuel_prize_pool_' + parimutuel_all_events_json_temp.details[i].game_id + '">' + prize_pool + '</span></div></div><div class="hasCountdown timer_div_for_768_up" style="margin: auto; width: auto; padding: 0; margin-top: 10px; background-color: transparent !important; height: 105px !important;"><div class="gold" style="font-weight: bold; text-align: center; font-size: 15px; padding: 9px;">ENDING IN</div>' + timer_text + '</div><button class="auto_bet_start_stop_button" onclick="javascript:OpenParimutuelGame(\x27' + parimutuel_all_events_json_temp.details[i].game_id + '\x27)" style="padding:10px; margin-top: 10px;">BET NOW</button> </div></li>');
            $('.time_remaining_' + parimutuel_all_events_json_temp.details[i].game_id).countdown({
                until: +time_remaining_parimutuel,
                format: 'DHMS'
            });
        }
        change_box_size_parimutuel();
    }
}
function showSelectedBettingCategory() {
    var category = $("#betting_category_container").val();
    LoadParimutuelEvents(category);
}
function LoadParimutuelBetsMain() {
    if (userid > 0) {
        $("#please_wait_loading_page").show();
        $.get('/cf_stats_public/?f=parimutuel_betting3', function(data) {
            if (typeof data != 'undefined') {
                if (data.status == "success") {
                    parimutuel_all_events_json = data;
                    var parimutuel_categories_append = '<option value="main">Select Category</option><option value="main">All</option><option value="popular">Popular</option><option value="new">New</option><option value="ending_soon">Ending Soon</option>';
                    for (var x = 0; x < data.categories.length; x++) {
                        parimutuel_categories_append = parimutuel_categories_append + '<option value="' + data.categories[x].code + '">' + data.categories[x].name + '</option>';
                    }
                    parimutuel_categories_append = parimutuel_categories_append + '<option value="pending">Pending</option><option value="expired">Expired</option><option value="my_bets">My Bets</option><option value="my_expired_bets">My Expired Bets</option>';
                    $("#betting_category_container").html(parimutuel_categories_append);
                    var parimutuel_betting_category_in_url = getParameterByName('category');
                    if (typeof parimutuel_betting_category_in_url != 'undefined' && parimutuel_betting_category_in_url.length > 0) {
                        $("#betting_category_container").val(parimutuel_betting_category_in_url);
                        LoadParimutuelEvents(parimutuel_betting_category_in_url);
                    } else {
                        LoadParimutuelEvents('main');
                    }
                }
                $("#please_wait_loading_page").hide();
            }
        });
    }
}
function ClosePromoBanner() {
    $('#deposit_promo_message_mobile').hide();
    $('#deposit_promo_message_regular').hide();
}
function CloseDailyJPBanner() {
    $('.daily_jackpot_main_container_div').hide();
}
function GenerateCaptchasNetCaptcha(parent_div, type, rand) {
    if (typeof rand !== "undefined" && rand.length > 5 && rand.length < 100) {
        GenCaptchasNetCaptcha(parent_div, type, rand);
    } else {
        $.get('/cgi-bin/api.pl?op=generate_captchasnet&f=' + fingerprint, function(data) {
            if (data.length < 100) {
                GenCaptchasNetCaptcha(parent_div, type, data);
            }
        });
    }
}
function GenCaptchasNetCaptcha(parent_div, type, random_string) {
    parent_div = parent_div.replace(/\s/g, '');
    var image_url = "//captchas.freebitco.in/cgi-bin/captcha_generator?client=freebitcoin&random=" + random_string;
    if (type == 2) {
        image_url = "//captchas.freebitco.in/securimage/securimage/securimage_show.php?random=" + random_string;
    } else if (type == 3) {
        image_url = "//captchas.freebitco.in/botdetect/e/live/index.php?random=" + random_string;
    }
    $('#' + parent_div + ' .captchasnet_captcha_content').html('<img src="' + image_url + '" onerror="GenerateCaptchasNetCaptcha(\'' + parent_div + '\', ' + type + ', \'' + random_string + '\');">');
    $('#' + parent_div + ' .captchasnet_captcha_random').val(random_string);
    $('#' + parent_div + ' .captchasnet_captcha_refresh').attr("onclick", "GenerateCaptchasNetCaptcha('" + parent_div + "', " + type + ")");
    $('#' + parent_div + ' .captchasnet_captcha_audio').attr("onclick", "PlayCaptchasNetAudioCaptcha('" + random_string + "')");
    $('#' + parent_div + ' .captchasnet_captcha_input_box').val('');
}
function getParameterByName(name, url) {
    if (!url)
        url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)')
      , results = regex.exec(url);
    if (!results)
        return undefined;
    if (!results[2])
        return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}
