package org.example;

import org.example.erros.ErroAoSalvarProdutoException;
import org.example.erros.ProdutoNaoEncontradoException;

public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto salvar(Produto produto) {
        if (produto == null || produto.getNome() == null
                || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (produto.getPreco() <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        try {
            return repository.salvar(produto);
        } catch (RuntimeException e) {
            throw new ErroAoSalvarProdutoException(
                    "Falha ao salvar o produto", e);
        }
    }

    public Produto buscarPorId(Long id) {
        Produto produto = repository.buscarPorId(id);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado: " + id);
        }
        return produto;
    }
}
