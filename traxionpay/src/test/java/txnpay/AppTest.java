package txnpay;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test.
     */
    private String apiKey = "7)5dmcfy^dp*9bdrcfcm$k-n=p7b!x(t)_f^i8mxl@v_+rno*x";
    private String secretKey = "cxl+hwc%97h6+4#lx1au*ut=ml+=!fx85w94iuf*06=rf383xs";
    private TraxionPay traxionpay;

    public AppTest() {
        try {
            traxionpay = new TraxionPay(this.apiKey, this.secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFetchBanks() {
       JSONObject data = traxionpay.fetchBanks();
       JSONArray banks = (JSONArray) data.get("data");
       assertTrue(banks instanceof JSONArray);
       assertTrue(banks.size() > 0);
    }

    @Test
    void testFetchBankAccounts() {
       JSONObject data = traxionpay.fetchBanks();
       JSONArray bankAccounts = (JSONArray) data.get("data");
       assertTrue(bankAccounts instanceof JSONArray);
       assertTrue(bankAccounts.size() > 0);
    }
    
    @Test
    void testFetchOTP() {
        JSONObject data = traxionpay.fetchOTP();
        String code = data.get("code").toString();
        assertTrue(data.containsKey("code"));
        assertNotNull(code);
    }

    @Test
    void testLinkBankAccount() {
        try {
            JSONObject data = traxionpay.linkBankAccount(
                new LinkBankAccountData("161414", "savings", "John Doe", "123412341234"));

            assertNotNull(data);
            assertTrue(data.containsKey("id"));
            assertTrue(data.containsKey("bank_name"));
            assertTrue(data.containsKey("account_number"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCashIn() {
        Integer merchantId = 6328;
        String merchantRefNo = "ABC123DEF456";

        JSONObject jsonData = new JSONObject();
        jsonData.put("payment_code", merchantRefNo);
        
        String merchantAdditionalData = jsonData.toJSONString();
        Double amount = 1500.0;
        String description = "My test payment";
        String statusNotificationUrl = "https://devapi.traxionpay.com/callback/";
        String successPageUrl = "https://devapi.traxionpay.com/callback";
        String failurePageUrl = "https://devapi.traxionpay.com/callback";
        String cancelPageUrl = "https://devapi.traxionpay.com/callback";
        String pendingPageUrl = "https://devapi.traxionpay.com/callback";
        String paymentMethod = ""; // Optional
        String currency = ""; // Optional

        JSONObject data = traxionpay.cashIn(
            new CashInData(
                merchantId, 
                merchantRefNo, 
                merchantAdditionalData, 
                amount, 
                description, 
                statusNotificationUrl, 
                successPageUrl, 
                failurePageUrl, 
                cancelPageUrl, 
                pendingPageUrl, 
                paymentMethod, 
                currency
            ), null // Billing details, Optional
        );
        assertNotNull(data);
        assertTrue(data.containsKey("url"));
        assertTrue(data.get("url").toString().contains("https://dev.traxionpay.com/payme/?data="));
    }

    @Test
    void testCashOut() {
        JSONObject otp = traxionpay.fetchOTP();
        JSONObject data = traxionpay.cashOut(new CashOutData(otp.get("code").toString(), 150.0, 433));

        assertNotNull(data);
    }
}
