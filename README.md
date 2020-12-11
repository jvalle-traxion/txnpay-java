# TraxionPay Java SDK

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)

## Installation
Download traxionpay-java-sdk.jar file and add it to .classpath
```xml
<classpathentry kind="lib" path="path/to/traxionpay-java-sdk.jar"/>
```

## Usage

#### Initialize
After installing, initialize by importing the package and using the [public and secret keys](https://dev.traxionpay.com/developers-guide).
```java
import txnpay.TraxionPay;

TraxionPay traxionpay = new TraxionPay(apiKey, secretKey);
```
#### Cash in
```java
// Sample arguments are the bare minimum for cashIn
Integer merchantId = 6328;
String merchantRefNo = "ABC123DEF456";
String merchantAdditionalData = "eyJwYXltZW50X2NvZGUiOiAiQUJDMTIzREVGNDU2In0=";
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

```
#### Cash out
```java
JSONObject otp = traxionpay.fetchOTP();
JSONObject data = traxionpay.cashOut(new CashOutData(otp.get("code").toString(), 150.0, 433));
```
#### Link a bank account
```java
JSONObject data = traxionpay.linkBankAccount(new LinkBankAccountData("161414", "savings", "John Doe", "123412341234"));
```
#### Fetch Cash Out OTP
```java
JSONObject otp = traxionpay.fetchOTP();
```
#### Fetch bank accounts
```java
JSONObject bankAccounts = traxionpay.fetchBankAccounts();
```
#### Fetch banks
```java
JSONObject banks = traxionpay.fetchBanks();
```
