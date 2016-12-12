(ns ld37.font
  (:import [com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
            FreeTypeFontGenerator$FreeTypeFontParameter]
           [com.badlogic.gdx.graphics.g2d GlyphLayout]
           [com.badlogic.gdx Gdx]))

(defn gen-font [file size color]
  (let [gen (FreeTypeFontGenerator.
             (.internal Gdx/files file))]
    (.generateFont
     gen
     (doto (FreeTypeFontGenerator$FreeTypeFontParameter.)
         (-> .size (set! size))
         (-> .color (set! color))))))


(def glyph-layout (GlyphLayout.))

(defn draw-centered-text
  [sprite-batch font text width height]
  (.draw font sprite-batch text
         (float (/ (- width (.width (doto glyph-layout
                                      (.setText font text))))
                   2))
         (float height)))
