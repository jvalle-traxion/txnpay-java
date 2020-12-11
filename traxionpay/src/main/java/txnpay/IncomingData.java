package txnpay;

import org.json.simple.JSONObject;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

interface ProduceJSON {
    public String toJSON();
}

abstract class BillingDetails {
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String middleName;
    protected String phone;
    protected String mobile;
    protected String address;
    protected String address2;
    protected String city;
    protected String state;
    protected String zip;
    protected String country;
    protected String remark;
}

abstract class CashInDetails {
    protected Integer merchantId;
    protected String merchantRefNo;
    protected String merchantAdditionalData;
    protected Double amount;
    protected String currency;
    protected String description;
    protected String paymentMethod;
    protected String statusNotificationUrl;
    protected String successPageUrl;
    protected String failurePageUrl;
    protected String cancelPageUrl;
    protected String pendingPageUrl;
}

abstract class CashInWithBilling extends CashInDetails {
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String middleName;
    protected String phone;
    protected String mobile;
    protected String address;
    protected String address2;
    protected String city;
    protected String state;
    protected String zip;
    protected String country;
    protected String remark;
}

abstract class PayformDetails extends CashInWithBilling {
    protected String authHash;
    protected String secureHash;
    protected String alg;
}

class LinkBankAccountData implements ProduceJSON {
    private String bank;
    private String accountType;
    private String accountName;
    private String accountNumber;

    /**
     * LinkBankAccountData
     * @param bank
     * @param accountType   "savings" or "checkings"
     * @param accountName
     * @param accountNumber
     */
    public LinkBankAccountData(String bank, String accountType, String accountName, String accountNumber) throws Exception {
        if (StringUtils.equalsAny(accountType, "savings", "checkings")) {
            this.bank = bank;
            this.accountType = accountType;
            this.accountName = accountName;
            this.accountNumber = accountNumber;
        } else {
            throw new Exception("account_type must either be 'savings' or 'checkings'.");
        }
    }

    public String toJSON() {
        JSONObject params = new JSONObject();

        params.put("bank", this.bank);
        params.put("account_type", this.accountType);
        params.put("account_number", this.accountNumber);
        params.put("account_name", this.accountName);

        return new JSONObject(params).toJSONString();
    }
}

/** */
class CashInData extends CashInDetails {
    public CashInData(
        Integer merchantId,
        String merchantRefNo,
        String merchantAdditionalData,
        Double amount,
        String description,
        String statusNotificationUrl,
        String successPageUrl,
        String failurePageUrl,
        String cancelPageUrl,
        String pendingPageUrl,
        @Nullable String paymentMethod,
        @Nullable String currency
    ) {
        this.merchantId = merchantId;
        this.merchantRefNo = merchantRefNo;
        this.merchantAdditionalData = merchantAdditionalData;
        this.amount = amount;
        this.description = description;
        this.statusNotificationUrl = statusNotificationUrl;
        this.successPageUrl = successPageUrl;
        this.failurePageUrl = failurePageUrl;
        this.cancelPageUrl = cancelPageUrl;
        this.pendingPageUrl = pendingPageUrl;
        this.paymentMethod = Utils.getValidData(this.paymentMethod);
        this.currency = Utils.getValidData(this.currency, "PHP");
    }
}

class BillingDetailsData extends BillingDetails {
    public BillingDetailsData(
        @Nullable String email,
        @Nullable String firstName,
        @Nullable String lastName,
        @Nullable String middleName,
        @Nullable String phone,
        @Nullable String mobile,
        @Nullable String address,
        @Nullable String address2,
        @Nullable String city,
        @Nullable String state,
        @Nullable String zip,
        @Nullable String country,
        @Nullable String remark
    ) {
        this.email = Utils.getValidData(email);
        this.firstName = Utils.getValidData(firstName);
        this.lastName = Utils.getValidData(lastName);
        this.middleName = Utils.getValidData(middleName);
        this.phone = Utils.getValidData(phone);
        this.mobile = Utils.getValidData(mobile);
        this.address = Utils.getValidData(address);
        this.address2 = Utils.getValidData(address2);
        this.city = Utils.getValidData(city);
        this.state = Utils.getValidData(state);
        this.zip = Utils.getValidData(zip);
        this.country = Utils.getValidData(country, "PH");
        this.remark = Utils.getValidData(remark);
    }
}

class PayformData extends PayformDetails implements ProduceJSON {
    protected String authHash;
    protected String secureHash;
    protected String alg;

    public PayformData(CashInData cashIn, BillingDetailsData billing, String secureHash, String authHash, String alg) {
        this.merchantId = cashIn.merchantId;
        this.merchantRefNo = cashIn.merchantRefNo;
        this.merchantAdditionalData = cashIn.merchantAdditionalData;
        this.amount = cashIn.amount;
        this.currency = cashIn.currency;
        this.description = cashIn.description;
        this.paymentMethod = cashIn.paymentMethod;
        this.statusNotificationUrl = cashIn.statusNotificationUrl;
        this.successPageUrl = cashIn.successPageUrl;
        this.failurePageUrl = cashIn.failurePageUrl;
        this.cancelPageUrl = cashIn.cancelPageUrl;
        this.pendingPageUrl = cashIn.pendingPageUrl;
        this.email = billing == null ? "" : Utils.getValidData(billing.email);
        this.firstName = billing == null ? "" : Utils.getValidData(billing.firstName);
        this.lastName = billing == null ? "" : Utils.getValidData(billing.lastName);
        this.middleName = billing == null ? "" : Utils.getValidData(billing.middleName);
        this.phone = billing == null ? "" : Utils.getValidData(billing.phone);
        this.mobile = billing == null ? "" : Utils.getValidData(billing.mobile);
        this.address = billing == null ? "" : Utils.getValidData(billing.address);
        this.address2 = billing == null ? "" : Utils.getValidData(billing.address2);
        this.city = billing == null ? "" : Utils.getValidData(billing.city);
        this.state = billing == null ? "" : Utils.getValidData(billing.state);
        this.zip = billing == null ? "" : Utils.getValidData(billing.zip);
        this.country = billing == null ? "" : Utils.getValidData(billing.country);
        this.remark = billing == null ? "" : Utils.getValidData(billing.remark);
        this.secureHash = secureHash;
        this.authHash = authHash;
        this.alg = alg;
    }


    
    public String toJSON() {
        JSONObject params = new JSONObject();

        params.put("merchant_id", this.merchantId);
        params.put("merchant_ref_no", this.merchantRefNo);
        params.put("merchant_additional_data", this.merchantAdditionalData);
        params.put("amount", this.amount);
        params.put("currency", this.currency);
        params.put("description", this.description);
        params.put("billing_email", this.email);
        params.put("billing_first_name", this.firstName);
        params.put("billing_last_name", this.lastName);
        params.put("billing_middle_name", this.middleName);
        params.put("billing_phone", this.phone);
        params.put("billing_mobile", this.mobile);
        params.put("billing_address", this.address);
        params.put("billing_address2", this.address2);
        params.put("billing_city", this.city);
        params.put("billing_state", this.state);
        params.put("billing_zip", this.zip);
        params.put("billing_country", this.country);
        params.put("billing_remark", this.remark);
        params.put("payment_method", this.paymentMethod);
        params.put("status_notification_url", this.statusNotificationUrl);
        params.put("success_page_url", this.successPageUrl);
        params.put("failure_page_url", this.failurePageUrl);
        params.put("cancel_page_url", this.cancelPageUrl);
        params.put("pending_page_url", this.pendingPageUrl);
        params.put("secure_hash", secureHash);
        params.put("auth_hash", authHash);
        params.put("alg", "HS256");

        return params.toJSONString();
    }
}

/**
 * 
 */
class CashOutData implements ProduceJSON {
    private String OTP;
    private Double amount;
    private Integer bankAccount;
    
    public CashOutData (String OTP, Double amount, Integer bankAccount) {
        this.OTP = OTP;
        this.amount = amount;
        this.bankAccount = bankAccount;
    }

    public String toJSON() {
        JSONObject params = new JSONObject();

        params.put("OTP", this.OTP);
        params.put("amount", this.amount);
        params.put("bank_account", this.bankAccount);

        return params.toJSONString();
    }
    
}