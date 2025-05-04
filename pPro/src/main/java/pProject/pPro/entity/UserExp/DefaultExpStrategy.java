package pProject.pPro.entity.UserExp;

public class DefaultExpStrategy implements ExpStrategy {

	@Override
	public int plusExp(int Exp) {
		return Exp + 20;
	}

}
