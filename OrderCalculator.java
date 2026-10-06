import java.util.Locale;
import java.util.Scanner;

/**
 * Практическая работа №3-4: расчёт стоимости заказа.
 * Порядок: база = цена * количество -> скидка (в %) -> НДС от суммы ПОСЛЕ скидки.
 */
public class OrderCalculator {

    // ---- Все правила сосредоточены здесь (для индивидуального варианта меняется одна строка) ----
    private static final int    MIN_QUANTITY = 1;
    private static final int    MAX_QUANTITY = 10_000;
    private static final double MAX_PRICE    = 5_000_000.0;
    private static final double MIN_DISCOUNT = 0.0;
    private static final double MAX_DISCOUNT = 30.0;   // вар. 2/5/8: 15 / 30 / 45
    static final double VAT_RATE     = 25.0;   // Вариант 4: ставка НДС 25 %

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--selfcheck")) {
            SelfCheck.run();
            return;
        }
        Scanner in = new Scanner(System.in);

        System.out.print("Количество: ");
        Integer quantity = parseInteger(readLine(in));
        if (quantity == null) { System.out.println("Ошибка: количество должно быть целым числом."); return; }

        System.out.print("Цена за единицу (руб.): ");
        Double price = parseDecimal(readLine(in));
        if (price == null) { System.out.println("Ошибка: цена должна быть числом (например 1250.50)."); return; }

        System.out.print("Скидка (%): ");
        Double discountPercent = parseDecimal(readLine(in));
        if (discountPercent == null) { System.out.println("Ошибка: скидка должна быть числом."); return; }

        String error = validate(quantity, price, discountPercent);
        if (error != null) {                       // A1: при ошибке расчёт НЕ выполняется
            System.out.println("Ошибка: " + error);
            return;
        }

        double base       = calculateBase(quantity, price);
        double discount   = calculateDiscount(base, discountPercent);
        double afterDisc  = round2(base - discount);
        double vat        = calculateVat(afterDisc, VAT_RATE);
        double total      = round2(afterDisc + vat);

        System.out.printf(Locale.US, "Базовая стоимость: %.2f руб.%n", base);
        System.out.printf(Locale.US, "Скидка (%.1f%%):    %.2f руб.%n", discountPercent, discount);
        System.out.printf(Locale.US, "После скидки:      %.2f руб.%n", afterDisc);
        System.out.printf(Locale.US, "НДС (%.0f%%):        %.2f руб.%n", VAT_RATE, vat);
        System.out.printf(Locale.US, "ИТОГО:             %.2f руб.%n", total);
    }

    /** Читает строку; при конце ввода возвращает null (пустой/отсутствующий ввод). */
    private static String readLine(Scanner in) {
        return in.hasNextLine() ? in.nextLine() : null;
    }

    /** Целое число из строки или null, если строка пустая/null/не число (например "abc", "5.5"). */
    static Integer parseInteger(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Число с плавающей точкой или null; принимает и запятую ("1250,50"); NaN и Infinity отвергаются. */
    static Double parseDecimal(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            double v = Double.parseDouble(raw.trim().replace(',', '.'));
            return (Double.isNaN(v) || Double.isInfinite(v)) ? null : v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Возвращает текст ошибки или null, если всё корректно. Проверка ДО любых вычислений. */
    static String validate(int quantity, double price, double discountPercent) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY)
            return "количество должно быть от " + MIN_QUANTITY + " до " + MAX_QUANTITY + ".";
        if (Double.isNaN(price) || price <= 0 || price > MAX_PRICE)
            return "цена должна быть больше 0 и не более " + (long) MAX_PRICE + " руб.";
        if (Double.isNaN(discountPercent) || discountPercent < MIN_DISCOUNT || discountPercent > MAX_DISCOUNT)
            return "скидка должна быть от " + (int) MIN_DISCOUNT + " до " + (int) MAX_DISCOUNT + " %.";
        return null;
    }

    static double calculateBase(int quantity, double price) {
        return round2(price * quantity);
    }

    /** discountPercent — ПРОЦЕНТЫ, а не рубли. */
    static double calculateDiscount(double base, double discountPercent) {
        return round2(base * discountPercent / 100.0);
    }

    /** НДС считается от суммы после скидки. */
    static double calculateVat(double amountAfterDiscount, double vatPercent) {
        return round2(amountAfterDiscount * vatPercent / 100.0);
    }

    /** Округление до копеек (2 знака). */
    static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
