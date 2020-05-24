package br.com.js.base.helper;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.js.base.dto.WorkDTO;
import br.com.js.base.model.Work;

public class WorkTestHelper {

	public static Work getWork() {
		return getWork(null);
	}

	public static Work getWork(Long id) {
		// @formatter:off
		return Work.builder()
			.id(id)
			.name("Troca de Filtro de ar")
			.sku("100")
			.discount(BigDecimal.ZERO)
			.price(BigDecimal.TEN)
			.build();
		// @formatter:on
	}

	public static WorkDTO getWorkDTO() {
		return getWorkDTO(null);
	}

	public static WorkDTO getWorkDTO(Long id) {
		// @formatter:off
		return WorkDTO.builder()
	      .id(id)
	      .name("Troca de Filtro de ar")
	      .sku("100")
	      .discount(BigDecimal.ZERO)
	      .price(BigDecimal.TEN)
	      .build();
		// @formatter:on
	}

	public static ArrayList<Work> getWorkList() {
		// @formatter:off
		var work1 = getWork(1l);
		var work2 = getWork(2l);
		
		var works = new ArrayList<Work>();
		works.add(work1);
		works.add(work2);
		
		return works;
		// @formatter:on
	}
}
