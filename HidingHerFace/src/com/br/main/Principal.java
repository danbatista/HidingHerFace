package com.br.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import com.br.model.PropriedadesFace;
import com.br.service.ServiceDesfoqueImagem;
import com.br.service.ServiceCorteImagem;
import com.br.service.ServiceExtracaoFacesImagem;
import com.br.service.ServiceSobreposicaoImagem;
import com.br.util.Util;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String workingDir = System.getProperty("user.dir");
		   System.out.println("Current working directory : " + workingDir);
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		CascadeClassifier cascadeClassifier = new CascadeClassifier(System.getProperty("user.dir") + "/haarcascade_frontalface_alt.xml");

		Mat mat = Highgui
				.imread(System.getProperty("user.dir") +"/chaves.jpg");
		
		//faz a detecção das faces
		ServiceExtracaoFacesImagem serviceExtractFaces = new ServiceExtracaoFacesImagem();
		MatOfRect matOfRect = serviceExtractFaces.detectarFaces(cascadeClassifier, mat);
		
		//obtem os dados de onde estão as faces (altura, largura, posição x e y)
		List<PropriedadesFace> propsFaces = serviceExtractFaces.obterDadosFaces(matOfRect);
		
		BufferedImage imagemCorteDesfoque = Util.converterParaImage(mat);
		
		//desfoca a imagem
		ServiceDesfoqueImagem serviceBlur = new ServiceDesfoqueImagem();
		imagemCorteDesfoque = serviceBlur.DesfocarImagem(imagemCorteDesfoque);
		
		//corta os rostos da imagem desfocada, 
		ServiceCorteImagem serviceCrop = new ServiceCorteImagem();
		propsFaces = serviceCrop.CortarImagem(propsFaces, imagemCorteDesfoque);
		
		ServiceSobreposicaoImagem serviceOverlay = new ServiceSobreposicaoImagem();
		
		//obtem toda a imagem se efeitos
		BufferedImage imagemSemEfeitos = Util.converterParaImage(mat);
		
		//"cola" os rostos desfocados sobre a imagem original
		imagemCorteDesfoque = serviceOverlay.juntarImagens(propsFaces, imagemSemEfeitos);
		
		File outputfile = new File("chaves menor.jpg");
		
	    try {
			ImageIO.write(imagemCorteDesfoque, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
