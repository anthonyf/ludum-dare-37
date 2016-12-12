(ns ld37.core
  (:gen-class)
  (:import (com.badlogic.gdx ApplicationAdapter Gdx)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication
                                            LwjglApplicationConfiguration)
           (com.badlogic.gdx.graphics Color GL30 Texture)
           (org.lwjgl.input Keyboard)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.utils.viewport FitViewport))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]
            [ld37.snake :as snake]
            [ld37.jukebox :as j]
            [ld37.title-screen :as ts]))

(def assets [["images/head.png" Texture]
             ["images/straight.png" Texture]
             ["images/turn.png" Texture]
             ["images/tail.png" Texture]
             ["images/spider.png" Texture]
             ["images/crash.png" Texture]])

(def sounds [[:eat "sounds/chomp.mp3"]
             [:dead "sounds/scream.mp3"]
             [:meow "sounds/meow.mp3"]])

(defn make-application
  []
  (let [stage (atom nil)]
    (proxy [ApplicationAdapter]
        []
        (create []
          (proxy-super create)

          ;; load all assets
          (doseq [[file type & [param]] assets]
            (.load am/manager file type param))
          (.finishLoading am/manager)

          ;; load all sounds
          (doseq [[sym path] sounds]
            (j/load-sound! sym path))

          (reset! stage
                  (ts/make-title-screen-stage stage))
          (.setInputProcessor Gdx/input @stage))
        (render []
          (.glClearColor Gdx/gl 0 1 1 1)
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
  (let [[width height] c/screen-size
        config (doto (LwjglApplicationConfiguration.)
                 (-> .title (set! c/title))
                 (-> .width (set! width))
                 (-> .height (set! height))
                 (-> .resizable (set! true)))]
    (LwjglApplication. (make-application) config)
    (Keyboard/enableRepeatEvents true)))

#_ (-main)
