package imd.ufrn.br.cashbooks.extensions;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import imd.ufrn.br.cashbooks.interfaces.IRestricoesComprasPrazo;
import imd.ufrn.br.cashbooks.model.Movimentacao;
import imd.ufrn.br.cashbooks.service.MovimentacaoService;

public class RegraPequenosComerciantes implements IRestricoesComprasPrazo {
	
	private static int LIMITE_COMPRAS = 3;
	private static int LIMITE_DIAS = 30;
	
	@Autowired 
	MovimentacaoService serviceMovimentacao;

	@Override
	public LocalDate calcularDataLimite(Movimentacao mov) {
		return mov.getDataMovimentacao().plusDays(LIMITE_DIAS);
	}

	@Override
	public boolean validarMovimentacao(Movimentacao mov) {
		
		System.out.println(mov.getDataCobranca() + " " + mov.getDataCobranca().isAfter(calcularDataLimite(mov))	);
		if(mov.getDataCobranca().isAfter(calcularDataLimite(mov))) {
			return false;
		}
		
		int count=0;
		
		if(mov.getCliente() != null) {
			for(Movimentacao m : mov.getCliente().getMovimentacoes()) {
				if(!m.isPago()) {
					count++;
				}
			}
			System.out.println(count);
			if(count >= LIMITE_COMPRAS) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getLimite() {
		return LIMITE_COMPRAS;
	}

}
