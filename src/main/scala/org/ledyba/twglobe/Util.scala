package org.ledyba.twglobe

import javax.servlet.http.HttpServletRequest

object Util {
	def getApplicationRoot(request:HttpServletRequest) = {
		request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()
	}
}