(ns ld37.game-stage
  (:import (com.badlogic.gdx.utils.viewport FitViewport)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx Gdx
                             Input$Keys)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.actions Actions
                                                    TemporalAction))
  (:require [ld37.common :as c]
            [ld37.snake :as snake]
            [ld37.room :as r]
            [ld37.cat-actor :as ca]
            [ld37.food-actor :as f]
            [ld37.asset-manager :as am]
            [ld37.jukebox :as j]
            [ld37.hairballs :as h]))

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


(defn do-dead-animation
  [stage data game]
  (let [{:keys [game-over-fn]} @data
        {[[head-x head-y] & _] :snake} @game
        crash-actor (Image. (am/make-texture-drawable "images/crash.png"))
        action (proxy [TemporalAction] [2]
                 (end []
                   (game-over-fn)
                   (proxy-super end))
                 (update [percent]
                   ))]
    (.setOrigin crash-actor
                (/ (.getWidth crash-actor) 2)
                (/ (.getHeight crash-actor) 2))
    (.setScale crash-actor 1 1)
    (c/set-actor-game-position-centered crash-actor [head-x head-y])
    (.addActor stage crash-actor)
    (.addAction crash-actor action)
    (j/play-sound :dead)))

(defn game-tick!
  [stage data game]
  (let [{:keys [dead? next-move]} @data]
    (if (and (= :dead (:state @game))
             (not dead?))
      (do (swap! data assoc :dead? true)
          (do-dead-animation stage data game))
      (reset! game (if next-move
                     (do (reset! data (assoc @data :next-move nil))
                         (snake/move @game next-move))
                     (snake/move-forward @game))))))

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
  [game-over-fn]
  (let [game (atom (snake/setup-game))
        data (atom {:game-over-fn game-over-fn})
        [screen-width screen-height] c/screen-size
        stage (proxy [Stage]
                  [(FitViewport. screen-width screen-height)]
                  (act []
                    (update-game! this data game)
                    (proxy-super act))
                  (draw []
                    (proxy-super draw)))
        room (r/make-room-actor @game)
        cat (ca/make-cat-actor! game)
        hairballs (h/make-hairballs! game)
        food (f/make-food-actor! game)]
    (.addActor stage room)
    (.addActor stage cat)
    (.addActor stage food)
    (.addActor stage hairballs)
    stage))
