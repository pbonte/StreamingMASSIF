package idlab.massif.interfaces.core;

public interface WindowInf extends PipeLineElement {
	
	public void setWindowSize(int window);

	public void setWindowSize(int windowSize, int windowSlide);
}
