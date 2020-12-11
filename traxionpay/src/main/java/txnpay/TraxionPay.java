package txnpay;

import org.json.simple.JSONObject;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class TraxionPay {
    private String token;
    private String apiKey;
    private String secretKey;
    private BasicHeader authHeaders;

    /**
     * Core object for using TraxionPay's `cashIn` and `cashOut` functionalities.
     * See full documentation at <https://dev.traxionpay.com/developers-guide>.
     * 
     * @param apiKey
     * @param secretKey
     * @throws Exception
     */
    public TraxionPay(String apiKey, String secretKey) throws Exception {
        if (!apiKey.isBlank() && !secretKey.isBlank()) {
            this.token = Utils.generateToken(secretKey);
            this.secretKey = secretKey;
            this.apiKey = apiKey;
            this.authHeaders = new BasicHeader("Authorization", "Basic " + token);
        } else {
            throw new Exception("'apiKey' and 'secretKey' must not be null");
        }
    }

    /**
     * Cash In enables merchants to receive money through the application.
     * Through this feature, merchants receive payments and store it in their in-app wallet.
     *
     * https://devapi.traxionpay.com/payform-link
     * 
     * @param cashIn
     * @param billing
     * @return JSONObject
     */
    public JSONObject cashIn(CashInData cashIn, @Nullable BillingDetailsData billing) {
        String dataToHash = cashIn.merchantRefNo + cashIn.amount + Utils.getValidData(cashIn.currency, "PHP") + cashIn.description;
        String secureHash = Utils.hmacSha256Digest(dataToHash, this.secretKey);
        String authHash = Utils.hmacSha256Digest(this.apiKey, this.secretKey);

        PayformData rawPayform = new PayformData(cashIn, billing, secureHash, authHash, "HS256");
        byte[] encoded = Base64.getEncoder().encode(StringEscapeUtils.unescapeJava(rawPayform.toJSON()).getBytes());
        String decoded = new String(encoded);
        
        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("form_data", decoded));

        JSONObject data = Utils.request("POST", "/payform-link", null, null, payload);
        return data;
    }

    /**
     * Retrieves a list of usable banks.
     *
     * https://devapi.traxionpay.com/banks/
     * @return JSONObject
     */
    public JSONObject fetchBanks() {
        JSONObject data = Utils.request("GET", "/banks/", null, null, null);
        return data;
    }

    /**
     * Retrieves a list of usable bank accounts.
     *
     * https://devapi.traxionpay.com/payout/bank-account/
     * @return JSONObject
     */
    public JSONObject fetchBankAccounts() {
        JSONObject data = Utils.request("GET", "/payout/bank-account/", this.authHeaders, null, null);
        return data;
    }

    /**
     * Links or creates a new bank account.
     *
     * https://devapi.traxionpay.com/payout/bank-account/
     * 
     * @param params
     * @return JSONObject
     */
    public JSONObject linkBankAccount(LinkBankAccountData params) {
        String json = params.toJSON();
        JSONObject data = Utils.request("POST", "/payout/bank-account/", this.authHeaders, json, null);
        return data;
    }

    /**
     * Retrieves otp for `cashOut` method.
     *
     * https://devapi.traxionpay.com/bank-payout/get-otp/
     * @return JSONObject
     */
    public JSONObject fetchOTP() {
        JSONObject data = Utils.request("POST", "/payout/bank-payout/get-otp/", this.authHeaders, null, null);
        return data;
    }

    /**
     * The Cash Out feature allows merchants to physically retrieve the money stored in the in-app wallet.
     * To Cash Out, the merchant links a bank accout,
     * provides an OTP, and requests a payout to the bank.
     *
     * https://devapi.traxionpay.com/payout/bank-payout/
     * @param params
     * @return JSONObject
     */
    public JSONObject cashOut(CashOutData params) {
        String json = params.toJSON();
        JSONObject data = Utils.request("POST", "/payout/bank-payout/", this.authHeaders, json, null);
        return data;
    }
}