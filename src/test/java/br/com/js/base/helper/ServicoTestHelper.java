package br.com.js.base.helper;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.js.base.dto.ServicoDTO;
import br.com.js.base.model.Servico;

public class ServicoTestHelper {

	public static Servico getServico() {
		return getServico(null);
	}

	public static Servico getServico(Long id) {
		// @formatter:off
		return Servico.builder()
			.id(id)
			.descricao("Troca de Filtro de ar")
			.codigo("100")
			.desconto(BigDecimal.ZERO)
			.preco(BigDecimal.TEN)
			.build();
		// @formatter:on
	}

	public static ServicoDTO getServicoDTO() {
		return getServicoDTO(null);
	}

	public static ServicoDTO getServicoDTO(Long id) {
		// @formatter:off
		return ServicoDTO.builder()
				.id(id)
				.descricao("Troca de Filtro de ar")
				.codigo("100")
				.desconto(BigDecimal.ZERO)
				.preco(BigDecimal.TEN)
				.build();
		// @formatter:on
	}

	public static ArrayList<Servico> obterListaComDoisServicos() {
		// @formatter:off
		var servico1 = getServico(1l);
		var servico2 = getServico(2l);
		
		var servicos = new ArrayList<Servico>();
		servicos.add(servico1);
		servicos.add(servico2);
		
		return servicos;
		// @formatter:on
	}
}
