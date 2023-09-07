package com.example.eventinformationsystembackend.common;

public class EmailTexts {
    public static final String ACCOUNT_CONFIRMATION_EMAIL_TEMPLATE =
                        """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <meta charset="UTF-8">
                                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                    <title>Account Confirmation</title>
                                    <style>
                                        body, p, h1 {
                                            margin: 0;
                                            padding: 0;
                                        }
                                        body {
                                            font-family: Tahoma, Arial, sans-serif;
                                            background-color: #f4f4f4;
                                            padding: 20px;
                                        }
                                        .container {
                                            max-width: 600px;
                                            margin: 0 auto;
                                            background-color: #ffffff;
                                            padding: 20px;
                                            border-radius: 10px;
                                            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                                        }
                                        h1 {
                                            color: #333333;
                                            margin-bottom: 20px;
                                            text-align: center;
                                        }
                                        p {
                                            color: #555555;
                                            margin-bottom: 10px;
                                            font-size: 130%%;
                                        }
                                        .confirmation-link {
                                            display: inline-block;
                                            background-color: #007bff;
                                            color: #ffffff;
                                            text-decoration: none;
                                            padding: 10px 20px;
                                            border-radius: 5px;
                                            transition: background-color 0.3s;
                                        }
                                        .confirmation-link:hover {
                                            background-color: #0056b3;
                                        }
                                    </style>
                                </head>
                                <body>
                                    <div class="container">
                                        <h1>Account Confirmation</h1>
                                        <br>
                                        <p>Dear %s,</p>
                                        <p>Thank you for creating an account with us. To complete your account setup, please click the following link to confirm your email address:</p>
                                        <br>
                                        <center>
                                        <p><a class="confirmation-link" href="%s" style="font-size:130%%">Confirm My Account</a></p>
                                        </center>
                                        <br>
                                        <p>If the above link doesn't work, you can copy and paste the following URL into your browser:</p>
                                        <p><strong>%s</strong></p>
                                        <hr>
                                        <p>If you have any questions or need assistance, please don't hesitate to contact us via the support ticket page!</p>
                                        <br>
                                        <p>Thank you for choosing us!</p>
                                        <p>Best regards,</p>
                                        <p>The <b>Ticket Master</b> Team</p>
                                    </div>
                                </body>
                                </html>
                                """;

    public static final String ORDER_RECEIVED_EMAIL_TEMPLATE =
                        """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <style>
                                body {
                                    font-family: Tahoma, Arial, sans-serif;
                                    background-color: #f4f4f4;
                                    margin: 0;
                                    padding: 0;
                                }
                                .container {
                                    max-width: 850px;
                                    margin: 0 auto;
                                    padding: 20px;
                                    background-color: #ffffff;
                                    border-radius: 10px;
                                    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                                    position: relative;
                                }
                                .header {
                                    text-align: center;
                                    background-color: #e84848;
                                    padding: 20px;
                                    border-top-left-radius: 10px;
                                    border-top-right-radius: 10px;
                                    color: white;
                                }
                                .header img {
                                    width: 160px;
                                    height: 160px;
                                    margin-bottom: 10px;
                                    margin-top: 100px;
                                }
                                .order-received {
                                    text-align: center;
                                    margin: 20px 0;
                                    color: white;
                                }
                                .footer {
                                    margin-top: 20px;
                                    text-align: right;
                                    color: #777777;
                                }
                                h1 {
                                    color: #333333;
                                    margin-top: 0;
                                    font-size: 26px;
                                }
                                p {
                                    color: #000000;
                                    line-height: 1.5;
                                    margin-bottom: 15px;
                                    font-size: 20px;
                                }
                                table {
                                    width: 100%%;
                                    border-collapse: collapse;
                                    margin-top: 20px;
                                }
                                th {
                                    padding: 10px;
                                    text-align: center;
                                    border-bottom: 1px solid #dddddd;
                                    color: #ffffff;
                                    background-color: #e84848;
                                    font-size: 18px;
                                }
                                td {
                                    padding: 10px;
                                    text-align: center;
                                    border-bottom: 1px solid #dddddd;
                                    color: #000000;
                                    font-size: 18px;
                                }
                                .divider {
                                    border-top: 1px solid #dddddd;
                                    margin-top: 20px;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <img src="https://cdn-icons-png.flaticon.com/512/2611/2611215.png" alt="Shopping Cart Icon">
                                    <h1 style="color:white">Order Received!</h1>
                                    <p style="position: absolute; top: 20px; left: 50px; color: white; font-size: 24px;"><strong>Ticket Master</strong></p>
                                </div>
                                <br>
                                <div class="order-received">
                                    <p style="text-align:left">Dear %s,</p>
                                    <p style="text-align:left">Your order <b>%s</b> was successfully received by us!</p>
                                    <p style="text-align:left">You will receive your tickets as a separate email. If you don't receive such email in the span of 20 minutes after this email, please contact us!</p>
                                    <p style="text-align:left">We sincerely thank you for your order and for choosing our website for your ticket purchase. Below is the summary of your order:</p>
                                </div>
                                <br>
                                <table>
                                    <tr>
                                        <th>Event Image</th>
                                        <th>Event</th>
                                        <th>Location</th>
                                        <th>Ticket Quantity</th>
                                        <th>Ticket Single Price</th>
                                        <th>Ticket Total Price</th>
                                    </tr>  
                                    %s                                   
                                </table>                              
                                <div class="footer">
                                    <div>
                                        %s
                                    </div>
                                </div>                                                                           
                                <div class="divider">
                                    <p>If you have any questions or need assistance, please don't hesitate to contact us via the support ticket page!</p>
                                    <br>
                                    <p>Thank you for choosing us!</p>
                                    <p>Best regards,</p>
                                    <p>The <b>Ticket Master</b> Team</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """;
    //line 185-186, above class divider
    //<div class="order-received">
    // </div>
    public static final String SINGULAR_TICKET_PDF_TEMPLATE =
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Order Confirmation: Concert Tickets</title>
                        <style>
                          @page {
                            size: A4;
                            margin: 0;
                          }
                          body {
                            font-family: Verdana;
                            background-color: #f5f5f5;
                            margin: 0;
                            padding: 0;
                            min-height: 100vh;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                          }
                          .content {
                            width: 21cm;
                            height: 29.7cm;
                            padding: 20px;
                            background-color: #ffffff;
                            border-radius: 10px;
                            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
                            text-align: center;
                          }
                          .header h2 {
                            color: #333333;
                            margin-top: 0;
                          }
                          .info {
                            margin-bottom: 20px;
                            color: #666666;
                          }
                          .qr-code {
                            margin-top: 5px;
                          }
                          .qr-code img {
                            max-width: 100%%;
                            height: auto;
                          }
                          .footer {
                            margin-top: 20px;
                            color: #999999;
                          }
                        </style>
                        </head>
                        <body>
                        <div class="content">
                          <div class="header">
                            <h2>Concert Details</h2>
                          </div>
                          <div class="info">
                            <p>Event: %s</p>
                            <p>Date: %s</p>
                            <p>Location: %s</p>
                          </div>
                          <div class="qr-code">
                            <img src="data:image/png;base64,%s" alt="Concert Ticket QR Code">
                          </div>
                          <div class="footer">
                            <p>This email was sent to %s</p>
                          </div>
                        </div>
                        </body>
                        </html>                   
                        """;
    public static final String TICKETS_RECEIVED_EMAIL_TEMPLATE =
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                           <meta charset="UTF-8">
                           <meta http-equiv="X-UA-Compatible" content="IE=edge">
                           <meta name="viewport" content="width=device-width, initial-scale=1.0">
                           <title>Ticket Order Confirmation</title>
                           <style>
                               body {
                                   font-family: Tahoma, sans-serif;
                                   background-color: #f4f4f4;
                                   margin: 0;
                                   padding: 0;
                               }
                               .container {
                                   max-width: 600px;
                                   margin: 20px auto;
                                   padding: 20px;
                                   background-color: #ffffff;
                                   border-radius: 5px;
                                   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                               }
                               .header {
                                   text-align: center;
                                   margin-bottom: 20px;
                               }
                               .content {
                                   margin-bottom: 20px;
                                   color: #555555;
                                   font-size: 130%%;
                               }
                               .button {
                                   display: inline-block;
                                   padding: 10px 20px;
                                   background-color: #007bff;
                                   color: #ffffff;
                                   text-decoration: none;
                                   border-radius: 3px;
                               }
                               .p {
                                   color: #555555;
                                   font-size: 160%%;
                               }
                           </style>
                        </head>
                        <body>
                           <div class="container">
                               <div class="header">
                                   <h1>Ticket Order Confirmation</h1>
                               </div>
                               <br>
                               <div class="content">
                                   <p>Dear %s,</p>
                                   <p>The number of tickets that you have purchased have been successfully sent to you!</p>
                                   <p>The tickets are attached to this email. Please find them attached in PDF format.</p>
                                   <p><strong>Please note that each ticket has an unique QR Code, which must be scanned at the event entrance!</strong></p>
                               </div>
                               <div class="content">
                               <hr>
                                   <p>If you have any questions or need assistance, please don't hesitate to contact us via the support ticket page!</p>
                                   <br>
                                   <p>Thank you for choosing us!</p>
                               </div>
                               <div class="content">
                                   <p>Best regards,</p>
                                   <p>The <strong>Ticket Master</strong> Team</p>
                               </div>
                           </div>
                        </body>
                        </html>
                        """;
}
