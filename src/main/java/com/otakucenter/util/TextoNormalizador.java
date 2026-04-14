package com.otakucenter.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoNormalizador {

    private TextoNormalizador() {
    }

    public static String normalizarParaEmail(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace("'", "")
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", ".");
    }
}
