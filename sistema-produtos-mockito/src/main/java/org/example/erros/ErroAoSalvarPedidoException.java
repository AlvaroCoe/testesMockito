package org.example.erros;

public class ErroAoSalvarPedidoException extends RuntimeException {

    public ErroAoSalvarPedidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
