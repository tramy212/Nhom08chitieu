package org.o7planning.nhom8_quanlychitieu.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {

    public String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
