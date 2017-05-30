package com.qk.util;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 重写图片切换的方法
 * @author AN
 */
public class ImgJPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image image;

	// 初始化时，加载的图片1.jpg
	public ImgJPanel(String path) {
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
	
	// 实现图片的更新
	public void loadPhoto(Image img) {
		image = img;
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image == null)
			return;

//		//获取位置
//		int x = (Constant.WIDTH - Constant.QR_WIDTH) / 2;
//		int y = (Constant.HEIGHT - Constant.QR_HEIGHT) / 2;
		//将图片画在中间
		g.drawImage(image, 0, 0, this);

	}
}
