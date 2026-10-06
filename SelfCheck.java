/** Самопроверка граничных случаев. Запуск: java OrderCalculator --selfcheck */
public class SelfCheck {
    private static int failed = 0;

    static void run() {
        // 0. ВАРИАНТ 4: ставка НДС должна быть 25 %
        eq("вариант 4: VAT_RATE = 25", 25.0, OrderCalculator.VAT_RATE);
        // 1. типовое значение: 10 * 1000 = 10000; скидка 10% = 1000; после = 9000; НДС 25% = 2250; итого 11250
        eq("база", 10000.00, OrderCalculator.calculateBase(10, 1000));
        eq("скидка", 1000.00, OrderCalculator.calculateDiscount(10000, 10));
        eq("НДС 25% после скидки", 2250.00, OrderCalculator.calculateVat(9000, OrderCalculator.VAT_RATE));
        eq("итого", 11250.00, 9000 + OrderCalculator.calculateVat(9000, OrderCalculator.VAT_RATE));
        // НДС считается от суммы ПОСЛЕ скидки, а не от базы (иначе было бы 2500)
        ok("НДС не от базы", OrderCalculator.calculateVat(9000, 25) != OrderCalculator.calculateVat(10000, 25));
        // 2. нижняя граница (включена)
        ok("quantity=1", OrderCalculator.validate(1, 100, 0) == null);
        ok("quantity=0", OrderCalculator.validate(0, 100, 0) != null);
        ok("price=0.01", OrderCalculator.validate(1, 0.01, 0) == null);
        ok("price=0", OrderCalculator.validate(1, 0, 0) != null);
        ok("discount=0", OrderCalculator.validate(1, 100, 0) == null);
        ok("discount=-0.1", OrderCalculator.validate(1, 100, -0.1) != null);
        // 3. верхняя граница (включена)
        ok("quantity=10000", OrderCalculator.validate(10000, 100, 0) == null);
        ok("quantity=10001", OrderCalculator.validate(10001, 100, 0) != null);
        ok("price=5000000", OrderCalculator.validate(1, 5_000_000, 0) == null);
        ok("price=5000000.01", OrderCalculator.validate(1, 5_000_000.01, 0) != null);
        ok("discount=30", OrderCalculator.validate(1, 100, 30) == null);
        ok("discount=30.1", OrderCalculator.validate(1, 100, 30.1) != null);
        // 4. пустой / отсутствующий ввод -> контролируемая ошибка (null), а не исключение
        ok("пустой ввод: количество \"\"", OrderCalculator.parseInteger("") == null);
        ok("пробелы вместо количества", OrderCalculator.parseInteger("   ") == null);
        ok("нет ввода (null): количество", OrderCalculator.parseInteger(null) == null);
        ok("пустой ввод: цена \"\"", OrderCalculator.parseDecimal("") == null);
        ok("нет ввода (null): цена", OrderCalculator.parseDecimal(null) == null);
        // 4a. неверный формат: "abc" вместо числа
        ok("\"abc\" вместо количества", OrderCalculator.parseInteger("abc") == null);
        ok("\"abc\" вместо цены", OrderCalculator.parseDecimal("abc") == null);
        ok("\"5.5\" вместо целого количества", OrderCalculator.parseInteger("5.5") == null);
        ok("\"NaN\" вместо цены", OrderCalculator.parseDecimal("NaN") == null);
        // корректный разбор не сломан
        ok("\"10\" -> 10", Integer.valueOf(10).equals(OrderCalculator.parseInteger(" 10 ")));
        ok("\"1250,50\" -> 1250.5", Double.valueOf(1250.5).equals(OrderCalculator.parseDecimal("1250,50")));
        // 4b. NaN (результат неверного ввода)
        ok("price=NaN", OrderCalculator.validate(1, Double.NaN, 0) != null);
        // 5. округление: 0.1 * 3 не должно давать 0.30000000000000004
        eq("округление", 0.30, OrderCalculator.calculateBase(3, 0.1));

        System.out.println(failed == 0 ? "SelfCheck: ВСЕ ПРОВЕРКИ ПРОЙДЕНЫ" : "SelfCheck: ОШИБОК " + failed);
    }

    private static void eq(String n, double exp, double act) { ok(n + " (" + act + ")", Math.abs(exp - act) < 1e-9); }
    private static void ok(String n, boolean c) { if (!c) failed++; System.out.println((c ? "OK   " : "FAIL ") + n); }
}
