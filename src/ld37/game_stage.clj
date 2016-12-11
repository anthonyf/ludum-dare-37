(ns ld37.game-stage
  (:import (com.badlogic.gdx.utils.viewport FitViewport)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx Gdx Input$Keys))
  (:require [ld37.common :as c]
            [ld37.snake :as snake]
            [ld37.room :as r]
            [ld37.cat-actor :as ca]))

(defn input-direction [stage game]
  (cond
    (.isKeyPressed Gdx/input Input$Keys/LEFT)
    :left

    (.isKeyPressed Gdx/input Input$Keys/RIGHT)
    :right

    (.isKeyPressed Gdx/input Input$Keys/UP)
    :up

    (.isKeyPressed Gdx/input Input$Keys/DOWN)
    :down

    :else nil))

(defn game-tick!
  [stage data game]
  (let [{:keys [next-move]} @data]
    (reset! game (if next-move
                   (do (reset! data (assoc @data :next-move nil))
                       (snake/move @game next-move))
                   (snake/move-forward @game)))))

(defn update-game! [stage data game]
  (let [{:keys [::time-since-update]} @data
        delta-time (.getDeltaTime Gdx/graphics)
        elapsed-time (+ delta-time (or time-since-update 0.0))]

    (if-let [direction (input-direction stage game)]
      (reset! data (assoc @data :next-move direction)))

    (if (> elapsed-time c/game-speed)
      (do (reset! data (assoc @data ::time-since-update 0))
          (game-tick! stage data game))
      (reset! data (assoc @data ::time-since-update elapsed-time)))))

(defn make-game-stage
  [game]
  (let [data (atom {})
        [screen-width screen-height] c/screen-size
        stage (proxy [Stage]
                  [(FitViewport. screen-width screen-height)]
                  (act []
                    (update-game! this data game)
                    (proxy-super act))
                  (draw []
                    (proxy-super draw)))
        room (r/make-room-actor @game)
        cat (ca/make-cat-actor! game)]
    (.addActor stage room)
    (.addActor stage cat)
    stage))
