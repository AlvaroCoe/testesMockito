package org.example;

public class Pedido {

    private Produto produto;
    private int quantidade;
    private double valorTotal;

    public Pedido(Produto produto, int quantidade, double valorTotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getValorTotal() { return valorTotal; }
}
