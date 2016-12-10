(ns ld37.core
  (:gen-class)
  (:import (com.badlogic.gdx ApplicationAdapter Gdx)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication
                                            LwjglApplicationConfiguration)
           (com.badlogic.gdx.graphics Color GL30 Texture)
           (org.lwjgl.input Keyboard)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(def title "Catalingus")
(def screen-size [1920 1080])

(defn make-game-stage
  []
  (let [[screen-width screen-height] screen-size]
    (proxy [Stage]
        [(FitViewport. screen-width screen-height)])))

(defn make-application
  []
  (let [stage (atom nil)]
    (proxy [ApplicationAdapter]
        []
        (create []
          (proxy-super create)
          (reset! stage (make-game-stage)))
        (render []
          (.glClearColor Gdx/gl 0 0 0 1)
          (.glClear Gdx/gl GL30/GL_COLOR_BUFFER_BIT)
          (doto @stage
            .act
            .draw))
        (resize [width height]
          (proxy-super resize width height)
          (-> @stage
              .getViewport
              (.update width height true)))
        (dispose []
          (proxy-super dispose)
          (.dispose @stage)))))

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
