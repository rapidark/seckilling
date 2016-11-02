package com.zq.fin.seckill.web.image;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.zq.fin.seckill.dto.DataRseult;
import com.zq.fin.seckill.service.image.ImageService;
import com.zq.fin.seckill.util.ObjectUtil;

@Controller
@RequestMapping("/image")
public class ImageController {
	
	@Autowired
	private ImageService imageService;

	/**
	 * 显示相册页面
	 * @return
	 */
	@RequestMapping(value="/{userid}/list", method = RequestMethod.GET)
	public String pictureUploadGet(@PathVariable("userid") Long userid, String ownedSpace, HttpServletRequest request, Model model) {
		//获取所属空间对应的图片
		DataRseult<?> dataRseult = imageService.showImage(userid, ownedSpace);
		model.addAttribute("dataImage", dataRseult);
		return "image/list";
	}
	
	/**
	 * 查询图片
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{id}/detail" ,method = RequestMethod.GET)
	@ResponseBody
	public DataRseult<?> showDetail(@PathVariable("id") Long id, HttpServletRequest request){
		//通过id 查询图片信息
		DataRseult<?> dataRseult = imageService.showDetailImage(id);
		return dataRseult;
	}
	
	/**
	 * 删除图片
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{id}/delete" ,method = RequestMethod.GET)
	@ResponseBody
	public DataRseult<?> delete(@PathVariable("id") Long id, HttpServletRequest request){
		//通过id 查询图片信息
		DataRseult<?> dataRseult = imageService.deleteImage(id);
		return dataRseult;
	}
	
	/**
	 * 上传图片
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{userid}/upload", method = RequestMethod.POST)
	@ResponseBody
	public DataRseult<?> pictureUploadPost(@PathVariable("userid") Long userid, String ownedSpace, HttpServletRequest request, HttpServletResponse response) {
		DataRseult<?> dataRseult = null;
		InputStream inputStream = null;
		try {
			CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if(commonsMultipartResolver.isMultipart(request)){
				//将从页面中获取的request沾化为文件数据
				MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
				//获取，是从页面中哪个file，标签上传的文件
				MultipartFile multipartFile = multipartHttpServletRequest.getFile("images");
				//转化为文件输入流
				inputStream = multipartFile.getInputStream();
				//获取所属空间
				//开始上传云端文件
				dataRseult = imageService.upload(userid, ownedSpace, inputStream, multipartFile.getOriginalFilename());
				return dataRseult;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new DataRseult<>(false, "文件转化流异常");
		} finally {
			if(ObjectUtil.isNotEmpty(inputStream)){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new DataRseult<>(false, "文件上传失败");
	}
}
