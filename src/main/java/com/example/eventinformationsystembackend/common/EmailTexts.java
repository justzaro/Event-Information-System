package com.example.eventinformationsystembackend.common;

public class EmailTexts {
    public static final String ACCOUNT_CONFIRMATION_TEXT=
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Account Confirmation</title>\n" +
                    "    <style>\n" +
                    "        /* Reset some default styles */\n" +
                    "        body, p, h1 {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "        \n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            background-color: #f4f4f4;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 0 auto;\n" +
                    "            background-color: #ffffff;\n" +
                    "            padding: 20px;\n" +
                    "            border-radius: 10px;\n" +
                    "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
                    "        }\n" +
                    "        \n" +
                    "        h1 {\n" +
                    "            color: #333333;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        p {\n" +
                    "            color: #555555;\n" +
                    "            margin-bottom: 10px;\n" +
                    "      \t\tfont-size:120%%;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .confirmation-link {\n" +
                    "            display: inline-block;\n" +
                    "            background-color: #007bff;\n" +
                    "            color: #ffffff;\n" +
                    "            text-decoration: none;\n" +
                    "            padding: 10px 20px;\n" +
                    "            border-radius: 5px;\n" +
                    "            transition: background-color 0.3s;\n" +
                    "        }\n" +
                    "\n" +
                    "        .confirmation-link:hover {\n" +
                    "            background-color: #0056b3;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <h1>Account Confirmation</h1>\n" +
                    "        <p>Dear %s,</p>\n" + // Placeholder for recipient's name
                    "        <p>Thank you for creating an account with us. To complete your account setup, please click the following link to confirm your email address:</p>\n" +
                    "        <br>\n" +
                    "        <center>\n" +
                    "        <p><a class=\"confirmation-link\" href=\"%s\" style=\"font-size:130%%\">Confirm My Account</a></p>\n" + // Placeholder for confirmation link
                    "        </center>\n" +
                    "        <br>\n" +
                    "        <p>If the above link doesn't work, you can copy and paste the following URL into your browser:</p>\n" +
                    "        <p>%s</p>\n" +
                    "        <p>Thank you for choosing us!</p>\n" +
                    "        <p>Best regards,</p>\n" +
                    "        <p>The <b>Ticket Master</b> Team</p>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";
}
