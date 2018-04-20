package view;

import java.util.Calendar;

import dal.InsumoDAO;
import dal.ProdutoDAO;
import dal.VendaDAO;
import model.ComposicaoProduto;
import model.Insumo;
import model.ItemVenda;
import model.Producao;
import model.Produto;
import model.Venda;
import util.Calculos;
import util.Console;
import dal.ProducaoDAO;

public class Principal {

	private static ComposicaoProduto composicaoProduto = new ComposicaoProduto();
	private static Insumo insumo = new Insumo();
	private static ItemVenda itemVenda = new ItemVenda();
	private static Produto produto = new Produto();
	private static Venda venda = new Venda();

	public static void main(String[] args) {

		int opcao;

		do {
			cabecalhoMenuPrincipal();
			opcao = Console.readInt("Escolha uma das op��es para prosseguir: ");

			switch (opcao) {
			case 1:
				registroEntradaInsumos();
				break;
			case 2:
				registroProducao();
				break;
			case 3:
				registroVendas();
				break;
			case 4:
				cadastros();
				break;
			case 5:
				consultas();
				break;
			default:
				break;
			}
		} while (opcao != 9);
	}

	private static void cabecalhoMenuPrincipal() {
		System.out.println("--------------------------------------------");
		System.out.println("|            PRODU��O ALIMENTOS            |");
		System.out.println("|                                          |");
		System.out.println("|     1 - Registro de entrada de insumos   |");
		System.out.println("|     2 - Registro de produ��o             |");
		System.out.println("|     3 - Registro de vendas               |");
		System.out.println("|     4 - Cadastros                        |");
		System.out.println("|     5 - Consultas                        |");
		System.out.println("|     9 - Encerrar execu��o                |");
		System.out.println("|                                          |");
		System.out.println("--------------------------------------------");
		System.out.println();
	}

	private static void registroEntradaInsumos() {
		System.out.println("--------------------------------------------");
		System.out.println("|       REGISTRAR ENTRADA DE INSUMOS       |");
		System.out.println("--------------------------------------------");
		System.out.println();

		String nome = Console.readString("Digite o nome do insumo: ");

		insumo = InsumoDAO.buscarInsumoPorNome(nome);

		if (insumo != null) {
			double quantidade, valorTotal;
			quantidade = Console.readDouble(
					"Digite a quantidade que deseja dar entrada em estoque (em " + insumo.getUnidadeMedida() + "): ");
			valorTotal = Console.readDouble("Digite o valor total do insumo que est� sendo inclu�do: ");

			if (InsumoDAO.registrarEntradaInsumo(insumo, quantidade, valorTotal)) {
				System.out.println();
				System.out.println("Insumo registrado com sucesso!");
			} else {
				System.out.println();
				System.out.println("Entrada n�o registrada, quantidade e valor devem ser maiores que 0!");
			}
		} else {
			System.out.println();
			System.out
					.println("Insumo n�o encontrado, verifique se o mesmo est� cadastrado, o registro foi cancelado!");
		}
	}

	private static void registroProducao() {
		System.out.println("--------------------------------------------");
		System.out.println("|            REGISTRAR PRODU��O            |");
		System.out.println("--------------------------------------------");
		System.out.println();

		String nome = Console.readString("Digite o nome do produto: ");

		produto = ProdutoDAO.buscarProdutoPorNome(nome);

		if (produto != null) {
			double quantidade;

			quantidade = Console.readDouble("Digite a quantidade produzida (em " + produto.getUnidadeMedida() + "): ");

			if (ProducaoDAO.registrarNovaProducao(produto, quantidade)) {
				System.out.println();
				System.out.println("Produ��o registrada com sucesso!");
			} else {
				System.out.println();
				System.out.println(
						"Produ��o n�o registrada, quantidade produzida deve ser maior que 0 ou o estoque de insumos � insuficiente!");
			}
		} else {
			System.out.println();
			System.out
					.println("Produto n�o encontrado, verifique se o mesmo est� cadastrado, o registro foi cancelado!");
		}
	}

	private static void registroVendas() {
		System.out.println("--------------------------------------------");
		System.out.println("|              REGISTRAR VENDA             |");
		System.out.println("--------------------------------------------");
		System.out.println();

		venda = new Venda();

		String op, nomeProduto;
		double valorAcumuladoVenda = 0;
		double custoAcumuladoVenda = 0;

		do {
			produto = new Produto();
			itemVenda = new ItemVenda();
			nomeProduto = Console.readString("Digite o nome do produto: ");
			produto = ProdutoDAO.buscarProdutoPorNome(nomeProduto);
			double valorVenda = 0, custoVenda = 0;

			if (produto != null) {

				double quantidade = Console.readDouble("Digite a quantidade a ser vendida: ");
				valorVenda = Console.readDouble("Digite o valor de venda: ");
				custoVenda = (produto.getCustoTotalEstoque() / produto.getQuantidadeEstoque()) * quantidade;

				itemVenda.setProduto(produto);
				itemVenda.setQuantidade(quantidade);
				itemVenda.setDataDaAdicao(Calendar.getInstance());
				itemVenda.setValorVenda(valorVenda);
				itemVenda.setCustoVenda(custoVenda);
				if (ProdutoDAO.registrarSaidaProduto(produto, quantidade)) {
					venda.getItens().add(itemVenda);
					valorAcumuladoVenda += valorVenda;
					custoAcumuladoVenda += custoVenda;
				} else {
					System.out.println("Quantidade inv�lida ou estoque insuficiente para realizar venda!");
				}
			} else {
				System.out.println("Produto n�o encontrado!");
			}

			op = Console.readString("\nDeseja adicionar mais um produto na mesma venda (S/N)? ");
		} while (op.toUpperCase().equals("S"));

		if (valorAcumuladoVenda > 0) {
			venda.setCliente(Console.readString("Digite o nome do cliente: "));
			venda.setDataDaVenda(Calendar.getInstance());
			venda.setValorDaVenda(valorAcumuladoVenda);
			venda.setCustoDaVenda(custoAcumuladoVenda);

			VendaDAO.cadastrarVenda(venda);

			System.out.println();
			System.out.println("Venda cadastrada com sucesso!");
			System.out.println();
		} else {
			System.out.println("Nenhum produto foi adicionado para venda, a opera��o n�o foi realizada!");
		}
	}

	private static void cadastros() {

		int opcao;

		do {
			System.out.println("--------------------------------------------");
			System.out.println("|      CADASTRO DE INSUMOS E PRODUTOS      |");
			System.out.println("|                                          |");
			System.out.println("|       1 - Cadastrar novo insumo          |");
			System.out.println("|       2 - Cadastrar novo produto         |");
			System.out.println("|       3 - Renomear insumos               |");
			System.out.println("|       4 - Adicionar insumos a composi��o |");
			System.out.println("|       5 - Remover insumos da composi��o  |");
			System.out.println("|       0 - Retornar ao menu principal     |");
			System.out.println("|                                          |");
			System.out.println("--------------------------------------------");
			System.out.println();
			opcao = Console.readInt("Escolha uma das op��es para prosseguir: ");

			switch (opcao) {
			case 1:
				cadastrarNovoInsumo();
				break;
			case 2:
				cadastrarNovoProduto();
				break;
			case 3:
				renomearInsumo();
				break;
			case 4:
				alterarComposicaoAdicionarInsumo();
				break;
			case 5:
				alterarComposicaoExcluirInsumo();
				break;
			default:
				break;
			}
		} while (opcao != 0);

	}

	private static void cadastrarNovoInsumo() {
		System.out.println("--------------------------------------------");
		System.out.println("|          CADASTRAR NOVO INSUMO           |");
		System.out.println("--------------------------------------------");
		System.out.println();

		insumo = new Insumo();

		insumo.setNome(Console.readString("Digite o nome do insumo: "));
		insumo.setUnidadeMedida(Console.readString("Digite a unidade de medida do insumo (kg, un, litro, cm): "));
		insumo.setQuantidadeEstoque(0);
		insumo.setValorTotal(0);

		if (InsumoDAO.cadastrarInsumo(insumo)) {
			System.out.println();
			System.out.println("Insumo cadastrado com sucesso!");
			System.out.println();

		} else {
			System.out.println();
			System.out.println("Insumo j� cadastrado anteriormente!");
			System.out.println();
		}
	}

	private static void cadastrarNovoProduto() {
		System.out.println("--------------------------------------------");
		System.out.println("|          CADASTRAR NOVO PRODUTO          |");
		System.out.println("--------------------------------------------");
		System.out.println();

		produto = new Produto();

		produto.setNome(Console.readString("Digite o nome do produto: "));
		produto.setUnidadeMedida(Console.readString("Digite a unidade medida do produto (kg, un, litro, cm): "));
		produto.setQuantidadeEstoque(0);
		produto.setCustoTotalEstoque(0);

		if (ProdutoDAO.cadastrarProduto(produto)) {

			cadastroComposicaoProduto(produto);
			System.out.println();
			System.out.println("Produto cadastrado com sucesso!");
			System.out.println();
		} else {
			System.out.println();
			System.out.println("Produto j� cadastrado anteriormente!");
			System.out.println();
		}
	}
	
	private static void renomearInsumo(){
		System.out.println("--------------------------------------------");
		System.out.println("|             RENOMEAR INSUMO              |");
		System.out.println("--------------------------------------------");
		System.out.println();
		
		Insumo insumo, consulta;
		String novoNome;
		
		insumo = InsumoDAO.buscarInsumoPorNome(Console.readString("Digite o nome do insumo: "));
		
		if (insumo != null){
			novoNome = Console.readString("Digite o novo nome para o insumo: ");
			
			consulta = InsumoDAO.buscarInsumoPorNome(novoNome);
			
			if (consulta == null){
				insumo.setNome(novoNome);
				System.out.println("Insumo alterado com sucesso!");
			} else {
				System.out.println("Nome de insumo j� cadastrado no sistema, por favbor tente novamente!");
			}
		} else {
			System.out.println("Insumo n�o localizado, por favor tente novamente!");
		}
	}

	private static void alterarComposicaoAdicionarInsumo() {
		System.out.println("--------------------------------------------");
		System.out.println("|  ALTERAR COMPOSI��O - ADICIONAR INSUMO   |");
		System.out.println("--------------------------------------------");
		System.out.println();

		produto = ProdutoDAO.buscarProdutoPorNome(Console.readString("Digite o nome do produto: "));

		if (produto != null) {
			cadastroComposicaoProduto(produto);
			System.out.println();
			System.out.println("Produto alterado com sucesso!");
			System.out.println();
		} else {
			System.out.println();
			System.out.println("Produto n�o encontrado!");
			System.out.println();
		}

	}

	private static void alterarComposicaoExcluirInsumo() {
		System.out.println("--------------------------------------------");
		System.out.println("|   ALTERAR COMPOSI��O - EXCLUIR INSUMO    |");
		System.out.println("--------------------------------------------");
		System.out.println();

		String nomeProduto, nomeInsumo;

		nomeProduto = Console.readString("Digite o nome do produto: ");
		produto = ProdutoDAO.buscarProdutoPorNome(nomeProduto);

		if (produto != null) {
			nomeInsumo = Console.readString("Digite o nome do insumo: ");

			if (ProdutoDAO.excluirInsumoDentroProduto(nomeProduto, nomeInsumo)) {
				System.out.println("Composi��o do produto alterada com sucesso!");
			} else {
				System.out.println("Insumo n�o encontrado!");
			}
		} else {
			System.out.println("Produto n�o encontrado!");
		}
	}

	private static void cadastroComposicaoProduto(Produto produto) {

		composicaoProduto = new ComposicaoProduto();

		String nomeInsumo, opcao;

		do {
			composicaoProduto = new ComposicaoProduto();

			nomeInsumo = Console.readString("Digite o nome do insumo: ");
			insumo = InsumoDAO.buscarInsumoPorNome(nomeInsumo);
			if (insumo != null) {
				composicaoProduto.setInsumo(insumo);
				double quantidade = Console.readDouble("Digite a quantidade do insumo \"" + insumo.getNome() + "\" (em "
						+ insumo.getUnidadeMedida() + ") para produ��o de 1 " + produto.getUnidadeMedida() + " de "
						+ produto.getNome() + ": ");
				composicaoProduto.setQuantidade(quantidade);
				if (ProdutoDAO.cadastrarComposicaoProduto(produto, composicaoProduto)) {
					System.out.println("Insumo cadastrado com sucesso!");
				} else {
					System.out.println("Quantidade deve ser maior que 0!");
				}

			} else {
				System.out.println("Insumo n�o encontrado!");
			}
			composicaoProduto.setInsumo(insumo);

			opcao = Console.readString("Deseja adicionar mais algum insumo (S/N)?");
		} while (opcao.toUpperCase().equals("S"));
	}

	private static void consultas() {

		int opcao;

		do {
			System.out.println("--------------------------------------------");
			System.out.println("|                CONSULTAS                 |");
			System.out.println("|                                          |");
			System.out.println("|     1 - Insumo espec�fico                |");
			System.out.println("|     2 - Produto espec�fico               |");
			System.out.println("|     3 - Estoque Insumos                  |");
			System.out.println("|     4 - Estoque Produtos                 |");
			System.out.println("|     5 - Hist�rico produ��o               |");
			System.out.println("|     6 - Vendas                           |");
			System.out.println("|     0 - Retornar ao menu principal       |");
			System.out.println("|                                          |");
			System.out.println("--------------------------------------------");
			System.out.println();
			opcao = Console.readInt("Escolha uma das op��es para prosseguir: ");

			switch (opcao) {
			case 1:
				consultarInsumoEspecifico();
				break;
			case 2:
				cosultarProdutoEspecifico();
				break;
			case 3:
				consultarEstoqueInsumos();
				break;
			case 4:
				consultarEstoqueProdutos();
				break;
			case 5:
				consultarHistoricoProducao();
				break;
			case 6:
				consultarVendas();
				break;
			default:
				break;
			}
		} while (opcao != 0);

	}

	private static void consultarInsumoEspecifico() {

		System.out.println("--------------------------------------------");
		System.out.println("|       CONSULTAR INSUMO ESPEC�FICO        |");
		System.out.println("--------------------------------------------");
		System.out.println();

		String nome = Console.readString("Digite o nome do insumo: ");

		insumo = InsumoDAO.buscarInsumoPorNome(nome);

		if (insumo != null) {
			System.out.println("Id: " + insumo.getId());
			System.out.println("Nome insumo: " + insumo.getNome());
			System.out.println(
					"Quantidade em estoque (em " + insumo.getUnidadeMedida() + "): " + insumo.getQuantidadeEstoque());
			System.out.println("Valor total em estoque: " + insumo.getValorTotal());

			double valorMedio;

			try {
				valorMedio = insumo.getValorTotal() / insumo.getQuantidadeEstoque();
			} catch (Exception e) {
				valorMedio = 0;
			}
			if (insumo.getQuantidadeEstoque() == 0){
				valorMedio = 0;
			}
			System.out.println("Valor m�dio estoque: " + valorMedio);
		} else {
			System.out.println("Insumo n�o encontrado!");
		}
	}

	private static void cosultarProdutoEspecifico() {
		System.out.println("--------------------------------------------");
		System.out.println("|       CONSULTAR PRODUTO ESPEC�FICO       |");
		System.out.println("--------------------------------------------");
		System.out.println();

		String nome = Console.readString("Digite o nome do produto: ");

		produto = ProdutoDAO.buscarProdutoPorNome(nome);

		if (produto != null) {
			System.out.println("Id: " + produto.getId());
			System.out.println("Nome produto: " + produto.getNome());
			System.out.println(
					"Quantidade em estoque (em " + produto.getUnidadeMedida() + "): " + produto.getQuantidadeEstoque());
			System.out.println("Custo total em estoque: " + produto.getCustoTotalEstoque());

			double valorMedio;

			try {
				valorMedio = produto.getCustoTotalEstoque() / produto.getQuantidadeEstoque();
			} catch (Exception e) {
				valorMedio = 0;
			}
			if (produto.getQuantidadeEstoque() == 0){
				valorMedio = 0;
			}

			System.out.println("Valor m�dio em estoque: " + valorMedio);
			System.out.println("Composi��o: ");

			String listaInsumos = "";
			for (int i = 0; i < produto.getComposicao().size(); i++) {
				listaInsumos = listaInsumos + produto.getComposicao().get(i).getQuantidade()
						+ produto.getComposicao().get(i).getInsumo().getUnidadeMedida() + " "
						+ produto.getComposicao().get(i).getInsumo().getNome() + "\n";
			}
			System.out.println(listaInsumos);
			System.out.println();

			System.out.println(produto);
		} else {
			System.out.println("Produto n�o encontrado!");
		}

	}

	private static void consultarEstoqueInsumos() {

		System.out.println("--------------------------------------------");
		System.out.println("|        CONSULTAR ESTOQUE INSUMOS         |");
		System.out.println("--------------------------------------------");
		System.out.println();

		try {
			if (insumo.getId() > 0) {
				for (Insumo insumo : InsumoDAO.retornarInsumos()) {
					System.out.println("Id: " + insumo.getId());
					System.out.println("Nome insumo: " + insumo.getNome());
					System.out.println("Quantidade em estoque (em " + insumo.getUnidadeMedida() + "): "
							+ insumo.getQuantidadeEstoque());
					System.out.println("Valor total em estoque: " + insumo.getValorTotal());

					double valorMedio;

					try {
						valorMedio = insumo.getValorTotal() / insumo.getQuantidadeEstoque();
					} catch (Exception e) {
						valorMedio = 0;
					}
					if (insumo.getQuantidadeEstoque() == 0){
						valorMedio = 0;
					}

					System.out.println("Valor m�dio em estoque: " + valorMedio);
					System.out.println();
				}

			} else {
				System.out.println("Sem dados para listar!");
				System.out.println();
			}
		} catch (Exception e) {
			System.out.println("Sem dados para listar!");
			System.out.println();
		}

	}

	private static void consultarEstoqueProdutos() {
		System.out.println("--------------------------------------------");
		System.out.println("|        CONSULTAR ESTOQUE PRODUTOS        |");
		System.out.println("--------------------------------------------");
		System.out.println();

		try {
			if (produto.getId() > 0) {
				for (Produto produto : ProdutoDAO.retornarProdutos()) {

					System.out.println("Id: " + produto.getId());
					System.out.println("Nome produto: " + produto.getNome());
					System.out.println("Quantidade em estoque (em " + produto.getUnidadeMedida() + "): "
							+ produto.getQuantidadeEstoque());
					System.out.println("Custo total em estoque: " + produto.getCustoTotalEstoque());

					double valorMedio;

					try {
						valorMedio = produto.getCustoTotalEstoque() / produto.getQuantidadeEstoque();
					} catch (Exception e) {
						valorMedio = 0;
					}
					if (produto.getQuantidadeEstoque() == 0){
						valorMedio = 0;
					}

					System.out.println("Valor m�dio em estoque: " + valorMedio);
					System.out.println();

					System.out.println("Composi��o: ");

					String listaInsumos = "";
					for (int i = 0; i < produto.getComposicao().size(); i++) {
						listaInsumos = listaInsumos + produto.getComposicao().get(i).getQuantidade()
								+ produto.getComposicao().get(i).getInsumo().getUnidadeMedida() + " "
								+ produto.getComposicao().get(i).getInsumo().getNome() + "\n";
					}
					System.out.println(listaInsumos);
					System.out.println();
				}
			} else {
				System.out.println("Sem dados para listar!");
				System.out.println();
			}
		} catch (Exception e) {
			System.out.println("Sem dados para listar!");
			System.out.println();
		}
	}

	private static void consultarHistoricoProducao() {
		System.out.println("--------------------------------------------");
		System.out.println("|     CONSULTAR HISTORICO DE PRODU��O      |");
		System.out.println("--------------------------------------------");
		System.out.println();

		int registros = 0;
		
			for (Producao producao : ProducaoDAO.retornarProducao()) {

				registros =+ producao.getId();
				
				System.out.println("Id: " + producao.getId());
				System.out.println("Nome produto: " + producao.getProduto().getNome());
				System.out.println("Quantidade produzida (em " + producao.getProduto().getUnidadeMedida() + "): "
						+ producao.getQuantidade());
				System.out.println("Custo total da produ��o: " + producao.getCustoProducao());
				System.out.println("Custo m�dio da produ��o: " + producao.getCustoProducao() / producao.getQuantidade());
				System.out.println("Data da produ��o: " + producao.getDataDaProducao().getTime());
				System.out.println();
			}
			
			if (registros == 0){
				System.out.println("Sem dados para listar!");
				System.out.println();
			}
	}

	private static void consultarVendas() {
		System.out.println("--------------------------------------------");
		System.out.println("|             CONSULTAR VENDAS             |");
		System.out.println("--------------------------------------------");
		System.out.println();

		if (venda.getId() > 0) {
			for (Venda venda : VendaDAO.retornarVendas()) {

				System.out.println("Id: " + venda.getId());
				System.out.println("Cliente: " + venda.getCliente());
				System.out.println("Data da venda: " + venda.getDataDaVenda().getTime());
				System.out.println("Valor da venda: " + venda.getValorDaVenda());
				System.out.println("Custo da venda: " + venda.getCustoDaVenda());
				double lucro = Calculos.calcularLucro(venda.getValorDaVenda(), venda.getCustoDaVenda());
				if (lucro >= 0) {
					System.out.println("Lucro de: " + lucro);
				} else {
					System.out.println("Preju�zo de: " + lucro);
				}
				System.out.println("Composi��o venda: ");

				String listaProdutos = "";
				for (int i = 0; i < venda.getItens().size(); i++) {
					listaProdutos = listaProdutos + venda.getItens().get(i).getQuantidade() + venda.getItens().get(i).getProduto().getUnidadeMedida() + " " + venda.getItens().get(i).getProduto().getNome() + "\nSubtotal produto: "
							+ venda.getItens().get(i).getValorVenda() + "\n";
					System.out.println();
				}
				System.out.println(listaProdutos);
				System.out.println();

			}
		} else {
			System.out.println("Sem dados para listar!");
			System.out.println();
		}
	}
}

// agrega��o ------
// composi��o ------
// heran�a ------
// associa��o ------
