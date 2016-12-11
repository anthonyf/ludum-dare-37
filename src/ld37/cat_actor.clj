(ns ld37.cat-actor
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.actions Actions
                                                    MoveToAction))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]))

(defn make-cat-head-actor!
  [game]
  (let [last-head-position (atom nil)
        {:keys []} @game
        head (proxy [Image] [(.get am/manager "images/head.png" Texture)]
               (act [delta]
                 (let [{[[head-x head-y :as head-position] & _] :snake} @game
                       [screen-x screen-y] (c/game-to-screen-pos this head-position)]
                   (cond (nil? @last-head-position)
                         (do (.setPosition this screen-x screen-y)
                             (reset! last-head-position head-position))

                         (not= @last-head-position head-position)
                         (do (.addAction this (Actions/moveTo screen-x screen-y c/game-speed))
                             (reset! last-head-position head-position))

                         ;; do nothing, already animating
                         :else nil))
                 (proxy-super act delta)))]
    head))

(defn make-body-tile!
  [game index]
  (let [image (proxy [Image] [(am/make-texture-drawable "images/straight.png")]
                (act [delta]
                  (let [{:keys [snake]} @game
                        [head & body] snake
                        [x y] (nth body index)
                        [screen-x screen-y][(+ (- (* c/tile-width x)
                                                  (/ (.getWidth this) 2))
                                               (/ c/tile-width 2))
                                            (+ (- (* c/tile-width y)
                                                  (/ (.getHeight this) 2))
                                               (/ c/tile-width 2))]]
                    (.setPosition this screen-x screen-y))))]
    image))

(defn make-body-tiles!
  [game]
  (let [{:keys [snake]} @game
        [head & body] snake]
    (mapv (fn [index]
            (make-body-tile! game index))
          (range (count body)))))

(defn make-cat-actor!
  [game]
  (let [head (make-cat-head-actor! game)
        body-tiles (atom nil)
        cat-actor (proxy [Group] []
                    (act [delta]
                      (let [{[_ & body] :snake} @game]
                        (when-not (= (count @body-tiles) (count body))
                          ;; remove old body tiles
                          (when-not (empty? @body-tiles)
                              (doseq [body-tile @body-tiles]
                                (.remove body-tile))
                              (reset! body-tiles nil))
                          ;; add body tiles
                          (reset! body-tiles (make-body-tiles! game))
                          (doseq [body-tile @body-tiles]
                            (.addActorBefore this head body-tile))))
                      (proxy-super act delta)))]
    (.addActor cat-actor head)
    cat-actor))
