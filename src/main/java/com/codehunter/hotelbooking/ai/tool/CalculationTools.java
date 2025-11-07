package com.codehunter.hotelbooking.ai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationTools {
    @Tool(description = "Add two numbers")
    public BigDecimal add(@ToolParam(description = "First number") BigDecimal a,
                         @ToolParam(description = "Second number") BigDecimal b) {
        if (a == null || b == null) return null;
        return a.add(b);
    }

    @Tool(description = "Subtract second number from first number")
    public BigDecimal subtract(@ToolParam(description = "First number") BigDecimal a,
                              @ToolParam(description = "Second number") BigDecimal b) {
        if (a == null || b == null) return null;
        return a.subtract(b);
    }

    @Tool(description = "Calculate percentage of a number")
    public BigDecimal percentage(@ToolParam(description = "Base number") BigDecimal base,
                                @ToolParam(description = "Percentage value (e.g. 20 for 20%)") BigDecimal percent) {
        if (base == null || percent == null) return null;
        return base.multiply(percent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    @Tool(description = "Divide first number by second number")
    public BigDecimal divide(@ToolParam(description = "Dividend") BigDecimal a,
                            @ToolParam(description = "Divisor") BigDecimal b) {
        if (a == null || b == null) return null;
        if (BigDecimal.ZERO.compareTo(b) == 0) throw new ArithmeticException("Division by zero");
        return a.divide(b, RoundingMode.HALF_UP);
    }

    @Tool(description = "Multiply two numbers")
    public BigDecimal multiply(@ToolParam(description = "First number") BigDecimal a,
                              @ToolParam(description = "Second number") BigDecimal b) {
        if (a == null || b == null) return null;
        return a.multiply(b);
    }
}
