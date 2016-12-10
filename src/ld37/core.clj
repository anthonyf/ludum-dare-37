(ns ld37.core
  (:gen-class)
  (:import (com.badlogic.gdx ApplicationAdapter Gdx)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication
                                            LwjglApplicationConfiguration)
           (com.badlogic.gdx.graphics Color GL30 Texture)
           (org.lwjgl.input Keyboard)))

(def title "Catalingus")
(def screen-size [1920 1080])

(defn make-application
  []
  (proxy [ApplicationAdapter]
      []
      (create []
        (proxy-super create))
      (render []
        (.glClearColor Gdx/gl 0 0 0 1)
        (.glClear Gdx/gl GL30/GL_COLOR_BUFFER_BIT))
      (resize [width height])
      (dispose []
        (proxy-super dispose))))

(defn -main
  [& args]
  (let [[width height] screen-size
        config (doto (LwjglApplicationConfiguration.)
                 (-> .title (set! title))
                 (-> .width (set! width))
                 (-> .height (set! height))
                 (-> .resizable (set! true)))]
    (LwjglApplication. (make-application) config)
    (Keyboard/enableRepeatEvents true)))

#_ (-main)
