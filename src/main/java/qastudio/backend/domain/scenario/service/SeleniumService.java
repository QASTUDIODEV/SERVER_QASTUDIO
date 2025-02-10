package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.springframework.stereotype.Service;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

@Service
@RequiredArgsConstructor
public class SeleniumService {

    private final SeleniumWebSocketHandler seleniumWebSocketHandler;


}

