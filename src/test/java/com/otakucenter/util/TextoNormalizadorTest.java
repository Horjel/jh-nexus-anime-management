package com.otakucenter.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextoNormalizadorTest {

    @Test
    void deberiaNormalizarCaracteresAcentuadosParaEmail() {
        String resultado = TextoNormalizador.normalizarParaEmail("José Ñandú O'Reilly");

        assertEquals("jose.nandu.oreilly", resultado);
    }
}
