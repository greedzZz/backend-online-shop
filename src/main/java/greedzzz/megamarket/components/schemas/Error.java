package greedzzz.megamarket.components.schemas;

import lombok.Data;

@Data
public class Error {
    private final int code;
    private final String message;
}
