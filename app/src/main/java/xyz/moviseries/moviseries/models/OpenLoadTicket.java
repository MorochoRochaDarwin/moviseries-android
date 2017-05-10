package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class OpenLoadTicket {
    private String ticket, captcha_url, captcha_w, captcha_h, wait_time, valid_until;


    public OpenLoadTicket(String ticket, String captcha_url, String captcha_w, String captcha_h, String wait_time, String valid_until) {
        this.ticket = ticket;
        this.captcha_url = captcha_url;
        this.captcha_w = captcha_w;
        this.captcha_h = captcha_h;
        this.wait_time = wait_time;
        this.valid_until = valid_until;
    }


    public String getTicket() {
        return ticket;
    }

    public String getCaptcha_url() {
        return captcha_url;
    }

    public String getCaptcha_w() {
        return captcha_w;
    }

    public String getCaptcha_h() {
        return captcha_h;
    }

    public String getWait_time() {
        return wait_time;
    }

    public String getValid_until() {
        return valid_until;
    }
}
