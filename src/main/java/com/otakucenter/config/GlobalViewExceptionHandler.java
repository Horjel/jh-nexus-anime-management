package com.otakucenter.config;

import com.otakucenter.exception.BusinessRuleException;
import com.otakucenter.exception.ForbiddenOperationException;
import com.otakucenter.exception.ResourceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class GlobalViewExceptionHandler {

    @ExceptionHandler({BusinessRuleException.class, ForbiddenOperationException.class})
    public String handleBusinessException(
            RuntimeException exception,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", exception.getMessage());
            return "redirect:" + referer;
        }

        model.addAttribute("tituloError", "Operacion no permitida");
        model.addAttribute("mensajeError", exception.getMessage());
        return "error/general";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("tituloError", "Recurso no encontrado");
        model.addAttribute("mensajeError", exception.getMessage());
        return "error/general";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", exception.getMessage());
            return "redirect:" + referer;
        }

        model.addAttribute("tituloError", "Solicitud no valida");
        model.addAttribute("mensajeError", exception.getMessage());
        return "error/general";
    }
}
