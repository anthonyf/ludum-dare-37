(ns ld37.game-stage
  (:import (com.badlogic.gdx.utils.viewport FitViewport)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx Gdx Input$Keys))
  (:require [ld37.common :as c]
            [ld37.snake :as snake]
            [ld37.room :as r]))

(defn input-direction [stage game]
  (cond
    (.isKeyPressed Gdx/input Input$Keys/LEFT)
    :left

    (.isKeyPressed Gdx/input Input$Keys/RIGHT)
    :right

    (.isKeyPressed Gdx/input Input$Keys/UP)
    :up

    (.isKeyPressed Gdx/input Input$Keys/DOWN)
    :down))

(defn game-tick!
  [stage game]
  (reset! game (if-let [direction (input-direction stage game)]
                 (snake/move @game direction)
                 (snake/move-forward @game))))

(defn update-game! [stage
                    {:keys [::time-since-update] :as game}]
  (let [delta-time (.getDeltaTime Gdx/graphics)
        elapsed-time (+ delta-time (or time-since-update 1.0))]
    (if (> elapsed-time
           1.0)
      (do (reset! game (assoc @game ::time-since-update 0))
          (game-tick! stage game))
      (reset! game (assoc @game ::time-since-update elapsed-time)))))

(defn make-game-stage
  [game]
  (let [[screen-width screen-height] c/screen-size
        stage (proxy [Stage]
                  [(FitViewport. screen-width screen-height)]
                  (act []
                    (update-game! this game)
                    (proxy-super act))
                  (draw []
                    (proxy-super draw)))
        room (r/make-room-actor @game)]
    (.addActor stage room)
    ;;(.setPosition room (/ screen-width 2.0) (/ screen-height 2.0))
    stage))
