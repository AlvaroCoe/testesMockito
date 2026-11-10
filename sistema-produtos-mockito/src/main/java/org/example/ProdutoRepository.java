package org.example;

    public interface ProdutoRepository {
        Produto salvar(Produto produto);
        Produto buscarPorId(Long id);
    }


