import argparse
import os
import sys
import time
from pathlib import Path

from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait


DEFAULT_BASE_URL = "http://localhost:8080"
DEFAULT_ADMIN_USER = "admin"
DEFAULT_ADMIN_PASSWORD = "*****"
DEFAULT_BLOCKED_USER = "Sergio Delegado"
DEFAULT_BLOCKED_PASSWORD = "Alpargata96"
OUTPUT_DIR = Path(__file__).resolve().parent / "img"


def build_driver(headless: bool) -> webdriver.Chrome:
    options = Options()
    options.add_argument("--window-size=1600,1200")
    options.add_argument("--disable-gpu")
    options.add_argument("--hide-scrollbars")
    options.add_argument("--force-device-scale-factor=1")
    if headless:
        options.add_argument("--headless=new")

    return webdriver.Chrome(service=Service(), options=options)


def wait_for(driver: webdriver.Chrome, condition, timeout: int = 15):
    return WebDriverWait(driver, timeout).until(condition)


def save(driver: webdriver.Chrome, filename: str):
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    target = OUTPUT_DIR / filename
    driver.save_screenshot(str(target))
    print(f"[OK] {target}")


def run_capture_step(label: str, action):
    try:
        action()
        return True
    except Exception as exc:
        print(f"[WARN] Paso omitido ({label}): {exc}")
        return False


def clear_and_type(element, value: str):
    element.clear()
    element.send_keys(value)


def open_login(driver: webdriver.Chrome, base_url: str):
    driver.get(f"{base_url}/login")
    wait_for(driver, EC.presence_of_element_located((By.ID, "username")))


def login(driver: webdriver.Chrome, base_url: str, username: str, password: str):
    open_login(driver, base_url)
    clear_and_type(driver.find_element(By.ID, "username"), username)
    clear_and_type(driver.find_element(By.ID, "password"), password)
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()


def ensure_logged_in(driver: webdriver.Chrome, base_url: str, username: str, password: str):
    login(driver, base_url, username, password)
    try:
        wait_for(driver, EC.url_matches(f"{base_url}/?$"))
    except TimeoutException as exc:
        if "login?error" in driver.current_url or driver.current_url.rstrip("/") == f"{base_url}/login":
            raise RuntimeError(
                "No se pudo iniciar sesion con el usuario administrador. "
                "Ejecuta el script con --admin-user y --admin-password usando las credenciales reales activas."
            ) from exc
        raise
    wait_for(driver, EC.presence_of_element_located((By.TAG_NAME, "body")))


def capture_blocked_login(driver: webdriver.Chrome, base_url: str):
    driver.get(f"{base_url}/login?blocked")
    wait_for(driver, EC.visibility_of_element_located((By.CLASS_NAME, "message-error")))


def capture_page(driver: webdriver.Chrome, base_url: str, path: str, filename: str, wait_css: str = None):
    driver.get(f"{base_url}{path}")
    if wait_css:
        wait_for(driver, EC.presence_of_element_located((By.CSS_SELECTOR, wait_css)))
    else:
        wait_for(driver, EC.presence_of_element_located((By.TAG_NAME, "body")))
    time.sleep(0.8)
    save(driver, filename)


def capture_page_authenticated(
    driver: webdriver.Chrome,
    base_url: str,
    path: str,
    filename: str,
    username: str,
    password: str,
    wait_css: str = None,
):
    driver.get(f"{base_url}{path}")
    if "/login" in driver.current_url:
        ensure_logged_in(driver, base_url, username, password)
        driver.get(f"{base_url}{path}")

    if wait_css:
        wait_for(driver, EC.presence_of_element_located((By.CSS_SELECTOR, wait_css)))
    else:
        wait_for(driver, EC.presence_of_element_located((By.TAG_NAME, "body")))

    if "/login" in driver.current_url:
        raise RuntimeError(f"La captura {filename} termino redirigida al login.")

    time.sleep(0.8)
    save(driver, filename)


def capture_pedido_validation(driver: webdriver.Chrome, base_url: str, filename: str):
    driver.get(f"{base_url}/pedidos/nuevo")
    wait_for(driver, EC.presence_of_element_located((By.CSS_SELECTOR, "form button[type='submit']")))
    driver.find_element(By.CSS_SELECTOR, "form button[type='submit']").click()
    wait_for(driver, EC.presence_of_element_located((By.CSS_SELECTOR, ".field-error")))
    time.sleep(0.8)
    save(driver, filename)


def capture_bitacora(driver: webdriver.Chrome, base_url: str, filename: str, username: str, password: str):
    driver.get(f"{base_url}/usuarios")
    if "/login" in driver.current_url:
        ensure_logged_in(driver, base_url, username, password)
        driver.get(f"{base_url}/usuarios")
    wait_for(driver, EC.presence_of_element_located((By.TAG_NAME, "body")))
    driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
    time.sleep(1.0)
    if "/login" in driver.current_url:
        raise RuntimeError("La captura de bitacora termino redirigida al login.")
    save(driver, filename)


def capture_permission_error(driver: webdriver.Chrome, base_url: str, username: str, password: str, filename: str):
    login(driver, base_url, username, password)
    try:
        wait_for(driver, EC.url_matches(f"{base_url}/?$"))
    except TimeoutException as exc:
        if "login?error" in driver.current_url or driver.current_url.rstrip("/") == f"{base_url}/login":
            raise RuntimeError(
                "No se pudo iniciar sesion con el usuario de consulta. "
                "Ejecuta el script con --user-user y --user-password usando una cuenta USER valida."
            ) from exc
        raise
    driver.get(f"{base_url}/usuarios")
    wait_for(driver, EC.presence_of_element_located((By.TAG_NAME, "body")))
    time.sleep(0.8)
    save(driver, filename)


def main():
    parser = argparse.ArgumentParser(description="Captura automatica de pantallas de JH Nexus Anime")
    parser.add_argument("--base-url", default=DEFAULT_BASE_URL)
    parser.add_argument("--admin-user", default=os.getenv("JH_ADMIN_USER", DEFAULT_ADMIN_USER))
    parser.add_argument("--admin-password", default=os.getenv("JH_ADMIN_PASSWORD", DEFAULT_ADMIN_PASSWORD))
    parser.add_argument("--blocked-user", default=os.getenv("JH_BLOCKED_USER", DEFAULT_BLOCKED_USER))
    parser.add_argument("--blocked-password", default=os.getenv("JH_BLOCKED_PASSWORD", DEFAULT_BLOCKED_PASSWORD))
    parser.add_argument("--user-user", default=os.getenv("JH_USER_USER", "Joyux"))
    parser.add_argument("--user-password", default=os.getenv("JH_USER_PASSWORD", "Joyux2026A"))
    parser.add_argument("--headed", action="store_true", help="Ejecuta Chrome visible en lugar de headless")
    args = parser.parse_args()

    driver = None
    try:
        driver = build_driver(headless=not args.headed)

        run_capture_step("01-login", lambda: capture_page(driver, args.base_url, "/login", "01-login.png", "#username"))
        run_capture_step(
            "02-login-usuario-bloqueado",
            lambda: (capture_blocked_login(driver, args.base_url), save(driver, "02-login-usuario-bloqueado.png")),
        )

        ensure_logged_in(driver, args.base_url, args.admin_user, args.admin_password)
        run_capture_step(
            "03-dashboard",
            lambda: capture_page_authenticated(driver, args.base_url, "/", "03-dashboard.png", args.admin_user, args.admin_password, "body"),
        )
        run_capture_step(
            "04-categorias-lista",
            lambda: capture_page_authenticated(driver, args.base_url, "/categorias", "04-categorias-lista.png", args.admin_user, args.admin_password, "table"),
        )
        run_capture_step(
            "05-categorias-formulario",
            lambda: capture_page_authenticated(driver, args.base_url, "/categorias/nueva", "05-categorias-formulario.png", args.admin_user, args.admin_password, "form"),
        )
        run_capture_step(
            "06-productos-lista",
            lambda: capture_page_authenticated(driver, args.base_url, "/productos", "06-productos-lista.png", args.admin_user, args.admin_password, "table"),
        )
        run_capture_step(
            "07-producto-detalle",
            lambda: capture_page_authenticated(driver, args.base_url, "/productos/ver/1", "07-producto-detalle.png", args.admin_user, args.admin_password, "body"),
        )
        run_capture_step("08-validacion-formulario", lambda: capture_pedido_validation(driver, args.base_url, "08-validacion-formulario.png"))
        run_capture_step(
            "09-clientes-lista",
            lambda: capture_page_authenticated(driver, args.base_url, "/clientes", "09-clientes-lista.png", args.admin_user, args.admin_password, "table"),
        )
        run_capture_step(
            "10-cliente-detalle",
            lambda: capture_page_authenticated(driver, args.base_url, "/clientes/ver/1", "10-cliente-detalle.png", args.admin_user, args.admin_password, "body"),
        )
        run_capture_step(
            "11-pedidos-lista",
            lambda: capture_page_authenticated(driver, args.base_url, "/pedidos", "11-pedidos-lista.png", args.admin_user, args.admin_password, "table"),
        )
        run_capture_step(
            "12-pedido-detalle",
            lambda: capture_page_authenticated(driver, args.base_url, "/pedidos/ver/1", "12-pedido-detalle.png", args.admin_user, args.admin_password, "body"),
        )
        run_capture_step(
            "13-usuarios-panel",
            lambda: capture_page_authenticated(driver, args.base_url, "/usuarios", "13-usuarios-panel.png", args.admin_user, args.admin_password, "table"),
        )
        run_capture_step(
            "14-mi-cuenta",
            lambda: capture_page_authenticated(driver, args.base_url, "/mi-cuenta", "14-mi-cuenta.png", args.admin_user, args.admin_password, "body"),
        )
        run_capture_step(
            "15-bitacora-admin",
            lambda: capture_bitacora(driver, args.base_url, "15-bitacora-admin.png", args.admin_user, args.admin_password),
        )

        run_capture_step(
            "16-error-controlado",
            lambda: capture_permission_error(
                driver,
                args.base_url,
                args.user_user,
                args.user_password,
                "16-error-controlado.png",
            ),
        )

        print("Capturas completadas.")
        return 0
    except TimeoutException as exc:
        print(f"[ERROR] Timeout esperando una pantalla: {exc}", file=sys.stderr)
        return 1
    except Exception as exc:  # pragma: no cover
        print(f"[ERROR] No se pudieron generar las capturas: {exc}", file=sys.stderr)
        return 1
    finally:
        if driver is not None:
            driver.quit()


if __name__ == "__main__":
    sys.exit(main())
