package example;

class Env {
    String get(String name) {
        return System.getenv(name);
    }
}
