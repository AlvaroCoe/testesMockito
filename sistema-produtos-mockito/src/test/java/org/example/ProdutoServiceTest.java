package org.example;

import org.example.erros.ErroAoSalvarProdutoException;
import org.example.erros.ProdutoNaoEncontradoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto("Mouse", 49.90, 10);
    }

    // CT01 - Produto válido
    @Test
    void deveSalvarProdutoValido() {

        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertNotNull(resultado);
        assertEquals("Mouse", resultado.getNome());
        assertEquals(49.90, resultado.getPreco());
        assertEquals(10, resultado.getQuantidade());

        verify(repository).salvar(produto);
    }

    // CT02 - Preço negativo
    @Test
    void deveRejeitarPrecoNegativo() {

        Produto produtoInvalido = new Produto("Mouse", -10, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produtoInvalido));

        verify(repository, never()).salvar(any());
    }

    // CT03 - Preço igual a zero
    @Test
    void deveRejeitarPrecoZero() {

        Produto produtoInvalido = new Produto("Mouse", 0, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produtoInvalido));

        verify(repository, never()).salvar(any());
    }

    // CT04 - Nome vazio
    @Test
    void deveRejeitarNomeVazio() {

        Produto produtoInvalido = new Produto("", 49.90, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produtoInvalido));

        verify(repository, never()).salvar(any());
    }

    // CT05 - Nome nulo
    @Test
    void deveRejeitarNomeNulo() {

        Produto produtoInvalido = new Produto(null, 49.90, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produtoInvalido));

        verify(repository, never()).salvar(any());
    }

    // CT06 - Quantidade negativa
    @Test
    void deveRejeitarQuantidadeNegativa() {

        Produto produtoInvalido = new Produto("Mouse", 49.90, -1);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produtoInvalido));

        verify(repository, never()).salvar(any());
    }

    // CT07 - Quantidade igual a zero
    @Test
    void deveAceitarQuantidadeZero() {

        Produto produtoSemEstoque = new Produto("Mouse", 49.90, 0);

        when(repository.salvar(produtoSemEstoque))
                .thenReturn(produtoSemEstoque);

        Produto resultado = service.salvar(produtoSemEstoque);

        assertNotNull(resultado);
        assertEquals(0, resultado.getQuantidade());

        verify(repository).salvar(produtoSemEstoque);
    }

    // CT08 - Buscar produto existente
    @Test
    void deveBuscarProdutoExistente() {

        when(repository.buscarPorId(1L)).thenReturn(produto);

        Produto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Mouse", resultado.getNome());

        verify(repository).buscarPorId(1L);
    }

    // CT09 - Buscar produto inexistente
    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {

        when(repository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.buscarPorId(99L));

        verify(repository).buscarPorId(99L);
    }

    // CT10 - Falha de conexão ao salvar
    @Test
    void deveLancarExcecaoQuandoFalharAoSalvar() {

        doThrow(new RuntimeException("Falha na conexão"))
                .when(repository).salvar(any(Produto.class));

        assertThrows(ErroAoSalvarProdutoException.class,
                () -> service.salvar(produto));

        verify(repository).salvar(produto);
    }

    // CT11 (Atividade 3) - Produto com preço igual a zero
    // Já coberto pelo CT03 (deveRejeitarPrecoZero).

    // CT12 (Atividade 3) - Preço 0.01 (fronteira): deve ser ACEITO
    @Test
    void deveAceitarPrecoMinimoValido() {

        Produto produtoLimite = new Produto("Cabo", 0.01, 5);

        when(repository.salvar(produtoLimite)).thenReturn(produtoLimite);

        Produto resultado = service.salvar(produtoLimite);

        assertNotNull(resultado);
        assertEquals(0.01, resultado.getPreco(), 0.0001);

        verify(repository).salvar(produtoLimite);
    }

    // CT13 (Atividade 3) - Nome em branco ("   "): isBlank() = true -> rejeita
    @Test
    void deveRejeitarNomeEmBranco() {

        Produto produtoInvalido = new Produto("   ", 49.90, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produtoInvalido));

        verify(repository, never()).salvar(any());
    }

    // CT14 (Atividade 3) - Produto não encontrado: já coberto pelo CT09.
    // CT15 (Atividade 3) - Banco fora do ar: já coberto pelo CT10.

    // CT16 (Atividade 3) - any(Produto.class) aceita QUALQUER objeto Produto
    @Test
    void deveSalvarQualquerProdutoComAny() {

        when(repository.salvar(any(Produto.class))).thenReturn(produto);

        Produto outroProduto = new Produto("Teclado", 129.90, 5);
        Produto resultado = service.salvar(outroProduto);

        assertEquals("Mouse", resultado.getNome());

        verify(repository).salvar(outroProduto);
    }

    // CT17 (extra) - Produto nulo
    @Test
    void deveRejeitarProdutoNulo() {

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(null));

        verify(repository, never()).salvar(any());
    }
}
