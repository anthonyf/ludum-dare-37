(ns ld37.title-screen
  (:import (com.badlogic.gdx.utils.viewport FitViewport)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx Gdx Input$Keys)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Label$LabelStyle)
           (com.badlogic.gdx.graphics Color))
  (:require [ld37.common :as c]
            [ld37.font :as f]
            [ld37.game-stage :as gs]))

(def title-color (Color. 1 1 1 1))
(def selected-color (Color. 1 0.4 0 1))

(defn make-title-screen-stage
  [stage]
  (let [[screen-width screen-height] c/screen-size
        font140 (f/gen-font "fonts/garfield.ttf" 140 title-color)
        font80 (f/gen-font "fonts/garfield.ttf" 80 title-color)
        font60 (f/gen-font "fonts/garfield.ttf" 40 title-color)
        play-label (Label. "Play!" (Label$LabelStyle. font80 title-color))
        exit-label (Label. "Quit" (Label$LabelStyle. font80 title-color))
        selected-menu-item (atom play-label)
        stage (proxy [Stage]
                  [(FitViewport. screen-width screen-height)]
                  (act []
                    (cond (or (.isKeyJustPressed Gdx/input Input$Keys/DOWN)
                              (.isKeyJustPressed Gdx/input Input$Keys/UP))
                          (do (.setColor @selected-menu-item title-color)
                              (cond (= @selected-menu-item play-label)
                                    (reset! selected-menu-item exit-label)

                                    (= @selected-menu-item exit-label)
                                    (reset! selected-menu-item play-label))
                              (.setColor @selected-menu-item selected-color))

                          (.isKeyJustPressed Gdx/input Input$Keys/ENTER)
                          (cond (= @selected-menu-item play-label)
                                (do (reset! stage (gs/make-game-stage
                                                   (fn []
                                                     (reset! stage
                                                             (make-title-screen-stage stage))
                                                     (.setInputProcessor Gdx/input @stage))))
                                    (.setInputProcessor Gdx/input @stage))

                                (= @selected-menu-item exit-label)
                                (.exit Gdx/app)))
                    (proxy-super act))
                  (draw []
                    (proxy-super draw)))
        title (Label. c/title (Label$LabelStyle. font140 title-color))
        subtitle (Label. c/subtitle (Label$LabelStyle. font60 title-color))]
    (.setColor play-label selected-color)
    (.addActor stage title)
    (.addActor stage subtitle)
    (.addActor stage play-label)
    (.addActor stage exit-label)
    (.setPosition title
                  (- (/ screen-width 2)
                     (/ (.getWidth title) 2))
                  (- (* screen-height 0.8)
                     (/ (.getHeight title) 2)))
    (.setPosition subtitle
                  (- (/ screen-width 2)
                     (/ (.getWidth subtitle) 2))
                  (- (.getY title)
                     (+ (.getHeight subtitle)
                        (* 0.4 (.getHeight subtitle)))))
    (.setPosition play-label
                  (- (/ screen-width 2)
                     (/ (.getWidth play-label) 2))
                  (- (* screen-height 0.4)
                     (/ (.getHeight play-label) 2)))
    (.setPosition exit-label
                  (- (/ screen-width 2)
                     (/ (.getWidth exit-label) 2))
                  (- (.getY play-label)
                     (+ (.getHeight exit-label)
                        (* 0.4 (.getHeight exit-label)))))
    stage))
